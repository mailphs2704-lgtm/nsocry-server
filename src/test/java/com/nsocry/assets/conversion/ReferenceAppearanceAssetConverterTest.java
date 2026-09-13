package com.nsocry.assets.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nsocry.assets.AppearanceAssetBundle;
import com.nsocry.assets.AppearanceAssetCodec;
import org.junit.jupiter.api.Test;

class ReferenceAppearanceAssetConverterTest {
    @Test
    void convertsSevenOthersRowsAndReferenceMounts() throws Exception {
        AppearanceAssetBundle bundle = ReferenceAppearanceAssetConverter.convert(dump());

        assertEquals(1, bundle.jumpingHeads().size());
        assertEquals(1, bundle.normalHeads().size());
        assertEquals(1, bundle.coveredHeads().size());
        assertEquals(1, bundle.legs().size());
        assertEquals(1, bundle.jumpingBodies().size());
        assertEquals(1, bundle.normalBodies().size());
        assertEquals(1, bundle.coveredBodies().size());
        assertEquals(2, bundle.mounts().size());
        assertEquals(776, bundle.mounts().get(0).itemId());
        assertEquals(777, bundle.mounts().get(1).itemId());
        assertEquals(bundle, AppearanceAssetCodec.decode(AppearanceAssetCodec.encode(bundle)));
    }

    @Test
    void rejectsMissingAppearanceGroup() {
        assertThrows(IllegalArgumentException.class,
                () -> ReferenceAppearanceAssetConverter.convert(
                        dump().replace(row("leg", leg()), "")));
    }

    private static String dump() {
        return "INSERT INTO `others` (`id`, `name`, `value`) VALUES\n"
                + row("head_jump", part(1)) + ",\n"
                + row("head_normal", part(1)) + ",\n"
                + row("head_boc_dau", part(1)) + ",\n"
                + row("leg", leg()) + ",\n"
                + row("body_jump", part(2)) + ",\n"
                + row("body_normal", part(2)) + ",\n"
                + row("body_boc_dau", part(2)) + ",\n"
                + row("exp", "[0,1]") + ";";
    }

    private static String row(String name, String value) {
        return "(1,'" + name + "','" + value + "')";
    }

    private static String part(int id) {
        return "[{\"id\":" + id + ",\"small\":3,\"item\":[{\"id\":4,\"dx\":5,\"dy\":6}]}]";
    }

    private static String leg() {
        return "[{\"id\":7,\"small\":8}]";
    }
}
