package OSFEmod.cardModifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class O
    extends AbstractOSFEModifier
{
    public O(){}

    @Override
    public float modifyDamageFinal(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target)
    {
        return damage * 2;
    }

    @Override
    public void onInitialApplication(AbstractCard card)
    {
        card.exhaust = true;
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
