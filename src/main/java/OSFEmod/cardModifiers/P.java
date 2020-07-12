package OSFEmod.cardModifiers;

import OSFEmod.OSFEinit;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.ArrayList;

public class P
        extends AbstractOSFEModifier
{
    public static int potency = 3;
    public P()
    {
    }

    private static ArrayList<String> applicableList = new ArrayList<>();

    public boolean isApplicable(AbstractCard crd)
    {
        return applicableList.contains(crd.getClass().getName());
    }

    public static void setApplicable(String cardClass)
    {
        applicableList.add(cardClass);
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card)
    {
        return String.format(CardCrawlGame.languagePack.getUIString(OSFEinit.makeID(getLetter())).TEXT[0], rawDescription, potency);
    }
}
