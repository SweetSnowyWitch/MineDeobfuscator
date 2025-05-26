# MineDeobfuscator (script for Stack Deobfuscator)

### Notion: for forge mods just use MDG

## Getting Started

Change mapping type in `fabric/run/config/stackdeobf.json` to `"yarn"` or `"quilt"`

```shell
"mapping-type": "quilt"
```

Change minecraft version in `gradle/libs.versions.toml` to yours

```shell
minecraft = "1.19.2"
```

Run Minecraft Client

## Using Script

Change `mappingProvider` type in `fabric/src/main/java/dev/booky/stackdeobf/Main.java` at 24 line to `QuiltMappingProvider` or `YarnMappingProvider`
```shell
    mappingProvider = new QuiltMappingProvider(VERSION_DATA);
```

Select `Main.java` and run current file. Paste the path to your mod directory in console (it may be `\MDG\result\merged_mdk\src` if you just decompiled your mod) and wait. 
(If presented) Change in accessWidener 1 line to `accessWidener	v2	named`. 
