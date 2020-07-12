package OSFEmod.relics;

import OSFEmod.OSFEinit;
import OSFEmod.cardModifiers.*;
import OSFEmod.patches.OSFEField;
import OSFEmod.util.TextureLoader;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomRelic;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class OneStepFromSpire
    extends CustomRelic
{
    public static final String ID = OSFEinit.makeID("OneStepFromSpire");
    public static boolean regularUpgrade = false;
    public OneStepFromSpire()
    {
        super(ID, TextureLoader.getTexture("OSFEmod/images/relics/Ragnarok.png"), RelicTier.STARTER, LandingSound.MAGICAL);
    }

    public String getUpdatedDescription()
    {
        return DESCRIPTIONS[0];
    }

    public static boolean canUpgradeCard(boolean __result, AbstractCard __instance)
    {
        return __result || (AbstractDungeon.player != null && __instance.type != AbstractCard.CardType.CURSE && __instance.type != AbstractCard.CardType.STATUS);
    }

    public static boolean altUpgradeCard(AbstractCard __instance)
    {
        if (__instance.rarity == AbstractCard.CardRarity.SPECIAL || regularUpgrade)
        {
            __instance.upgraded = false;
            __instance.timesUpgraded++;
            OSFEField.upgradePath.set(__instance, OSFEField.upgradePath.get(__instance) + "+");
            try
            {
                Method initTitle = AbstractCard.class.getDeclaredMethod("initializeTitle");
                initTitle.setAccessible(true);
                initTitle.invoke(__instance);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
            regularUpgrade = false;
            return false;
        }
        Random resetRng = null;
        if (SingleCardViewPopup.isViewingUpgrade)
        {
            resetRng = AbstractDungeon.relicRng.copy();
        }
        List<OSFEmod.cardModifiers.AbstractOSFEModifier> modlist = OSFEinit.osfeList.stream().filter(mod -> mod.isApplicable(__instance)).collect(Collectors.toList());
        if (modlist.size() > 0)
        {
            AbstractOSFEModifier mod = (OSFEmod.cardModifiers.AbstractOSFEModifier) modlist.get(AbstractDungeon.relicRng.random(0, modlist.size() - 1)).makeCopy();
            if (SingleCardViewPopup.isViewingUpgrade)
            {
                AbstractDungeon.relicRng = resetRng;
            }
            CardModifierManager.addModifier(__instance, mod);
            upgradeName(__instance, mod.getLetter());
        }
        return true;
    }

    public static void upgradeName(AbstractCard __instance, String upgradeLetter)
    {
        if (__instance.timesUpgraded == 0) __instance.name += " ";
        __instance.name += upgradeLetter;
        ++__instance.timesUpgraded;
        __instance.upgraded = true;
        OSFEField.upgradePath.set(__instance, OSFEField.upgradePath.get(__instance) + upgradeLetter);
        try
        {
            Method initTitle = AbstractCard.class.getDeclaredMethod("initializeTitle");
            initTitle.setAccessible(true);
            initTitle.invoke(__instance);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

    public static ApplyPowerAction changePower(AbstractCard __cardInstance, ApplyPowerAction __actionInstance)
    {
        AbstractPower __powerInstance = (AbstractPower) ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "powerToApply");
        if (__powerInstance.ID.equals(WeakPower.POWER_ID))
        {
            int weakCount = (int) CardModifierPatches.CardModifierFields.cardModifiers.get(__cardInstance).stream().filter(cm -> cm instanceof W).count();
            __powerInstance.amount += weakCount;
            __actionInstance.amount += weakCount;
        }
        if (__powerInstance.ID.equals(VulnerablePower.POWER_ID))
        {
            int vulnCount = (int) CardModifierPatches.CardModifierFields.cardModifiers.get(__cardInstance).stream().filter(cm -> cm instanceof V).count();
            __powerInstance.amount += vulnCount;
            __actionInstance.amount += vulnCount;
        }
        if (__powerInstance.ID.equals(PoisonPower.POWER_ID))
        {
            int poisonCount = (int) CardModifierPatches.CardModifierFields.cardModifiers.get(__cardInstance).stream().filter(cm -> cm instanceof P).count() * P.potency;
            __powerInstance.amount += poisonCount;
            __actionInstance.amount += poisonCount;
        }
        return __actionInstance;
    }

    public static AbstractGameAction changeCardGen(AbstractCard __cardInstance, AbstractGameAction __actionInstance)
    {
        int shivCount = (int) CardModifierPatches.CardModifierFields.cardModifiers.get(__cardInstance).stream().filter(cm -> cm instanceof S).count() * S.potency;
        int upgradeCount = (int) CardModifierPatches.CardModifierFields.cardModifiers.get(__cardInstance).stream().filter(cm -> cm instanceof U).count();
        if (__actionInstance instanceof MakeTempCardInDrawPileAction)
        {
            if (upgradeCount > 0)
            {
                AbstractCard card = (AbstractCard) ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "cardToMake");
                for (int i = 0; i < upgradeCount; i++)
                {
                    card.upgrade();
                }
            }
            if (ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "cardToMake") instanceof Shiv) __actionInstance.amount += shivCount;
        }
        if (__actionInstance instanceof MakeTempCardInDiscardAction)
        {
            if (upgradeCount > 0)
            {
                AbstractCard card = (AbstractCard) ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "c");
                for (int i = 0; i < upgradeCount; i++)
                {
                    card.upgrade();
                }
            }
            if (ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "c") instanceof Shiv) ReflectionHacks.setPrivate(__actionInstance, __actionInstance.getClass(), "numCards", (int)ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "numCards") + shivCount);
        }
        if (__actionInstance instanceof MakeTempCardInHandAction)
        {
            if (upgradeCount > 0)
            {
                AbstractCard card = (AbstractCard) ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "c");
                for (int i = 0; i < upgradeCount; i++)
                {
                    card.upgrade();
                }
            }
            if (ReflectionHacks.getPrivate(__actionInstance, __actionInstance.getClass(), "c") instanceof Shiv) __actionInstance.amount += shivCount;
        }
        return __actionInstance;
    }

    public static void loadPlayerSave(AbstractPlayer p)
    {
        if (p == null) return;
        for (AbstractCard c : p.masterDeck.group)
        {
            List<AbstractCardModifier> modList = CardModifierPatches.CardModifierFields.cardModifiers.get(c).stream().filter(mod -> mod instanceof AbstractOSFEModifier).collect(Collectors.toList());
            if (modList.size() > 0)
            {
                c.name += " ";
                modList.forEach(mod ->
                {
                    c.name += ((AbstractOSFEModifier)mod).getLetter();
                });
                c.upgraded = true;
            }

            if (c.timesUpgraded > modList.size())
            {
                for (int i = 0; i < c.timesUpgraded - modList.size(); i++)
                {
                    OneStepFromSpire.regularUpgrade = true;
                    c.upgrade();
                }
            }
        }
    }
}
