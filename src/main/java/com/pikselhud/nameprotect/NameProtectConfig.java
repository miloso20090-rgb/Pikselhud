package com.pikselhud.nameprotect;

import com.google.gson.annotations.SerializedName;

public class NameProtectConfig {
    @SerializedName("mode")
    public NameProtectMode mode = NameProtectMode.OFF;
    public String replacement = "Player";
    public String friends = "";
}
