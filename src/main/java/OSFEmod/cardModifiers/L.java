package OSFEmod.cardModifiers;

import OSFEmod.OSFEinit;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class L
    extends AbstractOSFEModifier
{
    private static int potency = 1;

    public L(){}

    @Override
    public void onInitialApplication(AbstractCard card)
    {
        card.purgeOnUse = true;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action)
    {
        AbstractDungeon.player.increaseMaxHp(potency, true);
    }

    @Override
    public AbstractCardModifier makeCopy()
    {
        return new L();
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card)
    {
        return String.format(CardCrawlGame.languagePack.getUIString(OSFEinit.makeID(getLetter())).TEXT[0], rawDescription, potency);
    }

    private static ArrayList<String> applicableList = new ArrayList<>();

    public boolean isApplicable(AbstractCard crd)
    {
        return applicableList.contains(crd.getClass().getName());
    }

    public static void setApplicable(String cardID)
    {
        applicableList.add(cardID);
    }
}
