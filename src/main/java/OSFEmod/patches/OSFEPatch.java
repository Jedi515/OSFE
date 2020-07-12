package OSFEmod.patches;

import OSFEmod.cardModifiers.*;
import OSFEmod.relics.OneStepFromSpire;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;
import org.clapper.util.classutil.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;

@SpirePatch(clz = CardCrawlGame.class,method = SpirePatch.CONSTRUCTOR)
public class OSFEPatch
{
    public static void Raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException
    {
        ClassFinder finder = new ClassFinder();

        finder.add(new File(Loader.STS_JAR));

        for (ModInfo modInfo : Loader.MODINFOS) {
            if (modInfo.jarURL != null) {
                try {
                    finder.add(new File(modInfo.jarURL.toURI()));
                } catch (URISyntaxException e) {
                    // do nothing
                }
            }
        }

        // Get all classes for AbstractCards
        ClassFilter filter = new AndClassFilter(
                new NotClassFilter(new InterfaceOnlyClassFilter()),
                new ClassModifiersClassFilter(Modifier.PUBLIC),
                new OrClassFilter(
                        new org.clapper.util.classutil.SubclassClassFilter(AbstractCard.class),
                        (classInfo, classFinder) -> classInfo.getClassName().equals(AbstractCard.class.getName())
                )
        );

        ArrayList<ClassInfo> foundClasses = new ArrayList<>();
        finder.findClasses(foundClasses, filter);

        for (ClassInfo classInfo : foundClasses)
        {
            CtClass ctClass = ctBehavior.getDeclaringClass().getClassPool().get(classInfo.getClassName());

            for (CtMethod m : ctClass.getDeclaredMethods())
            {
                if (m.getName().equals("upgrade") && (!m.isEmpty() && !Modifier.isNative(m.getModifiers())))
                {
                    m.insertBefore("{" +
                            "if (" + OneStepFromSpire.class.getName() + ".altUpgradeCard(this)) return;" +
                            "}");
                }
                m.instrument(actionReplacer);
            }
        }
    }


    private static ExprEditor actionReplacer = new ExprEditor()
    {
        @Override
        public void edit(NewExpr e) throws CannotCompileException
        {
            if (e.getClassName().equals(ApplyPowerAction.class.getName()))
            {
                e.replace("$_ = " + OneStepFromSpire.class.getName() + ".changePower(this, $proceed($$));");
            }
            if (e.getClassName().equals(MakeTempCardInHandAction.class.getName()) ||
                    e.getClassName().equals(MakeTempCardInDrawPileAction.class.getName()) ||
                    e.getClassName().equals(MakeTempCardInDiscardAction.class.getName()))
            {
                e.replace("$_ = " + OneStepFromSpire.class.getName() + ".changeCardGen(this, $proceed($$));");
            }
        }
    };

    @SpirePatch(clz = AbstractCard.class, method = "canUpgrade")
    public static class canUpgradeAll
    {
        public static boolean Postfix(boolean __result, AbstractCard __instance)
        {
            return OneStepFromSpire.canUpgradeCard(__result, __instance);
        }
    }
}
