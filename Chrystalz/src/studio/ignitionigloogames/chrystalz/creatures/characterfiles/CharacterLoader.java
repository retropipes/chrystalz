/*  Chrystalz: A dungeon-crawling, roguelike game
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/IgnitionIglooGames/chrystalz
 */
package studio.ignitionigloogames.chrystalz.creatures.characterfiles;

import java.io.File;
import java.io.IOException;

import studio.ignitionigloogames.chrystalz.Chrystalz;
import studio.ignitionigloogames.chrystalz.VersionException;
import studio.ignitionigloogames.chrystalz.creatures.party.PartyMember;
import studio.ignitionigloogames.chrystalz.dungeon.Extension;
import studio.ignitionigloogames.common.dialogs.CommonDialogs;
import studio.ignitionigloogames.common.fileio.FileIOException;
import studio.ignitionigloogames.common.fileio.FileIOReader;
import studio.ignitionigloogames.common.fileio.FileIOWriter;

public class CharacterLoader {
    private static PartyMember loadCharacter(final String name) {
        final String basePath = CharacterRegistration.getBasePath();
        final String loadPath = basePath + File.separator + name
                + Extension.getCharacterExtensionWithPeriod();
        try (FileIOReader loader = new FileIOReader(loadPath, "character")) {
            return PartyMember.read(loader);
        } catch (VersionException | FileIOException e) {
            CharacterRegistration.autoremoveCharacter(name);
            return null;
        } catch (final IOException e) {
            Chrystalz.getErrorLogger().logError(e);
            return null;
        }
    }

    public static PartyMember[] loadAllRegisteredCharacters() {
        final String[] registeredNames = CharacterRegistration
                .getCharacterNameList();
        if (registeredNames != null) {
            final PartyMember[] res = new PartyMember[registeredNames.length];
            // Load characters
            for (int x = 0; x < registeredNames.length; x++) {
                final String name = registeredNames[x];
                final PartyMember characterWithName = CharacterLoader
                        .loadCharacter(name);
                if (characterWithName != null) {
                    res[x] = characterWithName;
                } else {
                    // Auto-removed character
                    return CharacterLoader.loadAllRegisteredCharacters();
                }
            }
            return res;
        }
        return null;
    }

    public static void saveCharacter(final PartyMember character) {
        final String basePath = CharacterRegistration.getBasePath();
        final String name = character.getName();
        final String characterFile = basePath + File.separator + name
                + Extension.getCharacterExtensionWithPeriod();
        try (FileIOWriter saver = new FileIOWriter(characterFile,
                "character")) {
            character.write(saver);
        } catch (final IOException e) {
            Chrystalz.getErrorLogger().logError(e);
        }
    }

    static void deleteCharacter(final String name, final boolean showResults) {
        final String basePath = CharacterRegistration.getBasePath();
        final String characterFile = basePath + File.separator + name
                + Extension.getCharacterExtensionWithPeriod();
        final File toDelete = new File(characterFile);
        if (toDelete.exists()) {
            final boolean success = toDelete.delete();
            if (success) {
                if (showResults) {
                    CommonDialogs.showDialog("Character removed.");
                } else {
                    CommonDialogs.showDialog("Character " + name
                            + " autoremoved due to version change.");
                }
            } else {
                if (showResults) {
                    CommonDialogs.showDialog("Character removal failed!");
                } else {
                    CommonDialogs.showDialog(
                            "Character " + name + " failed to autoremove!");
                }
            }
        } else {
            if (showResults) {
                CommonDialogs.showDialog(
                        "The character to be removed does not have a corresponding file.");
            } else {
                CommonDialogs.showDialog(
                        "The character to be autoremoved does not have a corresponding file.");
            }
        }
    }
}
