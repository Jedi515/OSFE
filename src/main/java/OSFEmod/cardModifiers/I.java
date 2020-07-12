package OSFEmod.cardModifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class I
    extends AbstractOSFEModifier
{
    public I(){}

    @Override
    public void onInitialApplication(AbstractCard card)
    {
        card.isInnate = true;
    }

    private static ArrayList<String> applicableList = new ArrayList<>();

    public boolean isApplicable(AbstractCard crd)
    {
        return applicableList.contains(crd.getClass().getName()) && !crd.isInnate && !crd.getClass().getName().contains("tempCards");
    }

    public static void setApplicable(String cardID)
    {
        applicableList.add(cardID);
    }

}
