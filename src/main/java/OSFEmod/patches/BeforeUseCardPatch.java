package OSFEmod.patches;

import OSFEmod.cardModifiers.X;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(clz = AbstractPlayer.class, method = "useCard")
public class BeforeUseCardPatch
{
    @SpireInsertPatch(locator = Locator.class)
    public static void Insert(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse)
    {
        c.energyOnUse += CardModifierPatches.CardModifierFields.cardModifiers.get(c).stream().filter(mod -> mod instanceof X).count() * 2;
    }

    private static class Locator extends SpireInsertLocator
    {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
            return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
