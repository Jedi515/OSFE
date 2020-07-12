package OSFEmod;

import OSFEmod.cardModifiers.AbstractOSFEModifier;
import OSFEmod.relics.OneStepFromSpire;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.helpers.RelicType;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import OSFEmod.cardModifiers.*;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import static basemod.BaseMod.loadCustomStrings;

@SpireInitializer
public class OSFEinit
    implements
        PostInitializeSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber
{
    public static ArrayList<AbstractOSFEModifier> osfeList = new ArrayList<>();
    public static String modID = "OSFE";

    public static void initialize()
    {
        BaseMod.subscribe(new OSFEinit());
    }

    @Override
    public void receiveEditStrings() {
        loadStrings("eng");
        if (Settings.language != Settings.GameLanguage.ENG)
        {
            loadStrings(Settings.language.toString().toLowerCase());
        }
    }

    private void loadStrings(String langKey)
    {
        if (!Gdx.files.internal("OSFEmod/localization/" + langKey).exists())
        {
            System.out.println("OSFE MOD: Language not found: " + langKey);
            return;
        }

        loadCustomStrings(RelicStrings.class, GetLocString(langKey, "relicStrings"));
        loadCustomStrings(UIStrings.class, GetLocString(langKey, "UIStrings"));
    }

    private static String GetLocString(String locCode, String name) {
        return Gdx.files.internal("OSFEmod/localization/" + locCode + "/" + name + ".json").readString(
                String.valueOf(StandardCharsets.UTF_8));
    }



    private static ExprEditor powerCatcher = new ExprEditor()
    {
        @Override
        public void edit(NewExpr e) throws CannotCompileException
        {
            if (e.getClassName().equals(VulnerablePower.class.getName()))
            {
                V.setApplicable(e.getEnclosingClass().getName());
            }
            if (e.getClassName().equals(WeakPower.class.getName()))
            {
                W.setApplicable(e.getEnclosingClass().getName());
            }
            if (e.getClassName().equals(PoisonPower.class.getName()))
            {
                P.setApplicable(e.getEnclosingClass().getName());
            }
            if (e.getClassName().equals(Shiv.class.getName()))
            {
                S.setApplicable(e.getEnclosingClass().getName());
            }
            if (e.getClassName().contains("MakeTempCardIn"))
            {
                U.setApplicable(e.getEnclosingClass().getName());
            }
        }
    };

    public static String makeID(String id_in)
    {
        return modID + ":" + id_in;
    }

    @Override
    public void receivePostInitialize()
    {
        System.out.println("Post initialize should be called.");
        new AutoAdd(modID).packageFilter(AbstractOSFEModifier.class).any(AbstractOSFEModifier.class, (info, mod) -> osfeList.add(mod));

        CardLibrary.getAllCards().forEach(c ->
        {
            if (c.type == AbstractCard.CardType.ATTACK)
            {
                OSFEmod.cardModifiers.D.setApplicable(c.getClass().getName());
                if (!c.exhaust) O.setApplicable(c.getClass().getName());
            }
            if (!c.isInnate) I.setApplicable(c.getClass().getName());
            try
            {
                CtClass ctClass = Loader.getClassPool().get(c.getClass().getName());
                for (CtMethod ctMethod : ctClass.getDeclaredMethods())
                {
                    if (ctMethod.getName().equals("makeCopy")) continue;
                    ctMethod.instrument(powerCatcher);
                }
            } catch (NotFoundException | CannotCompileException e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void receiveEditRelics()
    {
        System.out.println("Edit relics should be called.");
        BaseMod.addRelic(new OneStepFromSpire(), RelicType.SHARED);
    }
}
