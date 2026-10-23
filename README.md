# Hush Mortem

A client-side Fabric mod for Minecraft. When a mob dies, the sounds it was still making stop with it: the enderman's stare, the creeper's fuse hiss, the pillager's crossbow loading.

The idea comes from [Shut Up Dead Entities](https://modrinth.com/mod/shut-up-dead-entities) by [diskria](https://github.com/diskria-minecraft/shut-up-dead-entities). Hush Mortem is a separate implementation written from scratch, with diskria's blessing.

## Requirements

- Minecraft 26.3 (26.2 and 26.1.2 planned)
- Fabric Loader 0.19.5 or newer
- Java 25
- Fabric API is **not** needed

The mod only runs on the client. Servers do not need it.

## How it works

1. Every hostile or neutral sound the client starts is linked to the mob that made it. Sounds attached to an entity are linked directly; positional sounds go to the nearest mob whose hit box (grown by one block) contains the sound's position.
2. When the server reports that a mob died, all of that mob's sounds still playing are stopped.
3. Sounds a mob starts during its death animation are stopped right away.

## Configuration

`config/hushmortem.properties`, created on first start:

| Key | Default | Meaning |
| --- | --- | --- |
| `keep_death_sound` | `true` | `true` lets the mob's death sound play out. `false` silences it as well. |

## Building

```
./gradlew build
```

The jar ends up in `build/libs/`.

## Authors

Claude AI, Konitor

## License

MIT, see [LICENSE](LICENSE).
