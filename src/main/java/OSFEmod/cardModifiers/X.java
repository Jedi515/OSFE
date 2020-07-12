package OSFEmod.cardModifiers;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;

public class X
        extends AbstractOSFEModifier
{
    public X()
    {
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action)
    {
    }

    private static ArrayList<String> applicableList = new ArrayList<>();

    public boolean isApplicable(AbstractCard crd)
    {
        return crd.cost == -1;
    }

    public static void setApplicable(String cardClass)
    {
        applicableList.add(cardClass);
    }
}
