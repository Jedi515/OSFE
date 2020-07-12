package OSFEmod.cardModifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class M
        extends AbstractOSFEModifier
{
    public M()
    {
    }

    @Override
    public void onInitialApplication(AbstractCard card)
    {
        super.onInitialApplication(card);
        card.cost--;
        card.setCostForTurn(card.costForTurn - 1);
    }

    private static ArrayList<String> applicableList = new ArrayList<>();

    public boolean isApplicable(AbstractCard crd)
    {
        return crd.cost > 0;
    }

    public static void setApplicable(String cardClass)
    {
        applicableList.add(cardClass);
    }
}
