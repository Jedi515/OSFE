package OSFEmod.cardModifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class V
    extends AbstractOSFEModifier
{
    public V(){}

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
