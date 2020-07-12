package OSFEmod.patches;

import OSFEmod.relics.OneStepFromSpire;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;


public class DontReupgradeCards
{
    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class DontReupgrade
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("upgrade"))
                    {
                        m.replace("{}");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "loadPlayerSave")
    public static class SetName
    {
        public static void Raw(CtBehavior method)
        {
            try
            {
                method.insertAfter("{" + OneStepFromSpire.class.getName() + ".loadPlayerSave(p);}");
            } catch (CannotCompileException e)
            {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class WeirdShit
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor()
            {
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("upgrade"))
                    {
                        m.replace("{((" + AbstractCard.class.getName() + ")$0).timesUpgraded = upgradeTime;}");
                    }
                }
            };
        }
    }
}