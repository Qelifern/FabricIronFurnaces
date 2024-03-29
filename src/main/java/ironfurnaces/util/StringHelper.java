package ironfurnaces.util;

import com.google.common.collect.Lists;
import ironfurnaces.init.Reference;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class StringHelper {


    public static List<String> displayEnergy(double energy, double capacity) {
        List<String> text = new ArrayList<String>();
        NumberFormat format = DecimalFormat.getNumberInstance();
        String i = format.format(energy);
        String j = format.format(capacity);
        i = i.replaceAll("\u00A0", ",");
        j = j.replaceAll("\u00A0", ",");
        text.add(i + " / " + j + " E");
        return text;
    }

    public static List<Text> getShiftInfoGui()
    {
        List<Text> list = Lists.newArrayList();
        list.add(new TranslatableText("tooltip.ironfurnaces.gui_close"));
        MutableText tooltip1 = new TranslatableText("tooltip." + Reference.MOD_ID + ".gui_hold_shift");
        MutableText shift = new LiteralText("[Shift]");
        MutableText tooltip2 = new TranslatableText("tooltip." + Reference.MOD_ID + ".gui_shift_more_options");
        tooltip1.formatted(Formatting.GRAY);
        shift.formatted(Formatting.GOLD, Formatting.ITALIC);
        tooltip2.formatted(Formatting.GRAY);
        list.add(tooltip1.append(shift).append(tooltip2));
        return list;
    }

    public static Text getShiftInfoText()
    {
        MutableText tooltip1 = new TranslatableText("tooltip." + Reference.MOD_ID + ".hold").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
        MutableText shift = new LiteralText("[Shift]").setStyle(Style.EMPTY.withFormatting(Formatting.GOLD).withFormatting(Formatting.ITALIC));
        MutableText tooltip2 = new TranslatableText("tooltip." + Reference.MOD_ID + ".for_details").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
        return tooltip1.append(shift).append(tooltip2);
    }

}
