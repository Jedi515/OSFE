package OSFEmod.cardModifiers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class D
    extends AbstractOSFEModifier
{
    private static ArrayList<String> applicableList = new ArrayList<>();
    public D(){}

    @Override
    public void onInitialApplication(AbstractCard card)
    {
        super.onInitialApplication(card);
        card.cost += 1;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action)
    {
        if (!card.purgeOnUse)
        {
            AbstractMonster m = null;
            if (action.target != null) {
                m = (AbstractMonster)action.target;
            }

            AbstractCard tmp = card.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToBottom(tmp);
            tmp.current_x = card.current_x;
            tmp.current_y = card.current_y;
            tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            tmp.target_y = (float)Settings.HEIGHT / 2.0F;
            if (m != null) {
                tmp.calculateCardDamage(m);
            }

            tmp.purgeOnUse = true;
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
        }
    }

    public boolean isApplicable(AbstractCard crd)
    {
        return applicableList.contains(crd.getClass().getName()) && crd.cost > -1;
    }

    public static void setApplicable(String cardID)
    {
        applicableList.add(cardID);
    }
}
