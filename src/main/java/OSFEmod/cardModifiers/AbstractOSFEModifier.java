package OSFEmod.cardModifiers;

import OSFEmod.OSFEinit;
import basemod.AutoAdd;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@AutoAdd.Ignore
public abstract class AbstractOSFEModifier
    extends AbstractCardModifier
{

    public String getLetter()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public AbstractCardModifier makeCopy()
    {
        try
        {
            return this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public boolean isInherent(AbstractCard card)
//    {
//        return true;
//    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card)
    {
        return String.format(CardCrawlGame.languagePack.getUIString(OSFEinit.makeID(getLetter())).TEXT[0], rawDescription);
    }

    public abstract boolean isApplicable(AbstractCard crd);
}
