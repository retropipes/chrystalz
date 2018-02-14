/*  Chrystalz: A dungeon-crawling, roguelike game
Licensed under MIT. See the LICENSE file for details.

All support is handled via the GitHub repository: https://github.com/IgnitionIglooGames/chrystalz
 */
package studio.ignitionigloogames.chrystalz.dungeon.utilities;

import java.io.IOException;
import java.util.ArrayList;

import studio.ignitionigloogames.chrystalz.dungeon.abc.AbstractGameObject;
import studio.ignitionigloogames.chrystalz.dungeon.objects.ArmorShop;
import studio.ignitionigloogames.chrystalz.dungeon.objects.ClosedDoor;
import studio.ignitionigloogames.chrystalz.dungeon.objects.Empty;
import studio.ignitionigloogames.chrystalz.dungeon.objects.HealShop;
import studio.ignitionigloogames.chrystalz.dungeon.objects.Ice;
import studio.ignitionigloogames.chrystalz.dungeon.objects.Monster;
import studio.ignitionigloogames.chrystalz.dungeon.objects.OpenDoor;
import studio.ignitionigloogames.chrystalz.dungeon.objects.Regenerator;
import studio.ignitionigloogames.chrystalz.dungeon.objects.SpellShop;
import studio.ignitionigloogames.chrystalz.dungeon.objects.StairsDown;
import studio.ignitionigloogames.chrystalz.dungeon.objects.StairsUp;
import studio.ignitionigloogames.chrystalz.dungeon.objects.Tile;
import studio.ignitionigloogames.chrystalz.dungeon.objects.Wall;
import studio.ignitionigloogames.chrystalz.dungeon.objects.WeaponsShop;
import studio.ignitionigloogames.chrystalz.manager.dungeon.FormatConstants;
import studio.ignitionigloogames.common.fileio.FileIOReader;

public class GameObjectList {
    // Fields
    private final ArrayList<AbstractGameObject> allObjectList;

    // Constructor
    public GameObjectList() {
        final AbstractGameObject[] allObjects = { new ArmorShop(),
                new ClosedDoor(), new Empty(), new HealShop(), new Ice(),
                new Monster(), new OpenDoor(), new Regenerator(),
                new SpellShop(), new Tile(), new Wall(), new WeaponsShop(),
                new StairsUp(), new StairsDown() };
        this.allObjectList = new ArrayList<>();
        // Add all predefined objects to the list
        for (final AbstractGameObject allObject : allObjects) {
            this.allObjectList.add(allObject);
        }
    }

    // Methods
    public AbstractGameObject[] getAllObjects() {
        return this.allObjectList
                .toArray(new AbstractGameObject[this.allObjectList.size()]);
    }

    public String[] getAllDescriptions() {
        final AbstractGameObject[] objects = this.getAllObjects();
        final String[] allDescriptions = new String[objects.length];
        for (int x = 0; x < objects.length; x++) {
            allDescriptions[x] = objects[x].getDescription();
        }
        return allDescriptions;
    }

    public final AbstractGameObject[] getAllRequired(final int layer) {
        final AbstractGameObject[] objects = this.getAllObjects();
        final AbstractGameObject[] tempAllRequired = new AbstractGameObject[objects.length];
        int x;
        int count = 0;
        for (x = 0; x < objects.length; x++) {
            if (objects[x].getLayer() == layer && objects[x].isRequired()) {
                tempAllRequired[count] = objects[x];
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            final AbstractGameObject[] allRequired = new AbstractGameObject[count];
            for (x = 0; x < count; x++) {
                allRequired[x] = tempAllRequired[x];
            }
            return allRequired;
        }
    }

    public final AbstractGameObject[] getAllWithoutPrerequisiteAndNotRequired(
            final int layer) {
        final AbstractGameObject[] objects = this.getAllObjects();
        final AbstractGameObject[] tempAllWithoutPrereq = new AbstractGameObject[objects.length];
        int x;
        int count = 0;
        for (x = 0; x < objects.length; x++) {
            if (objects[x].getLayer() == layer && !objects[x].isRequired()) {
                tempAllWithoutPrereq[count] = objects[x];
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            final AbstractGameObject[] allWithoutPrereq = new AbstractGameObject[count];
            for (x = 0; x < count; x++) {
                allWithoutPrereq[x] = tempAllWithoutPrereq[x];
            }
            return allWithoutPrereq;
        }
    }

    public final AbstractGameObject getNewInstanceByName(final String name) {
        final AbstractGameObject[] objects = this.getAllObjects();
        AbstractGameObject instance = null;
        int x;
        for (x = 0; x < objects.length; x++) {
            if (objects[x].getName().equals(name)) {
                instance = objects[x];
                break;
            }
        }
        if (instance == null) {
            return null;
        } else {
            return instance.clone();
        }
    }

    public AbstractGameObject readGameObject(final FileIOReader reader,
            final int formatVersion) throws IOException {
        final AbstractGameObject[] objects = this.getAllObjects();
        AbstractGameObject o = null;
        String UID = "";
        if (formatVersion == FormatConstants.MAZE_FORMAT_LATEST) {
            UID = reader.readString();
        }
        for (final AbstractGameObject object : objects) {
            AbstractGameObject instance;
            instance = object.clone();
            if (formatVersion == FormatConstants.MAZE_FORMAT_LATEST) {
                o = instance.readGameObjectV1(reader, UID);
                if (o != null) {
                    return o;
                }
            }
        }
        return null;
    }

    public AbstractGameObject readSavedGameObject(final FileIOReader reader,
            final String UID, final int formatVersion) throws IOException {
        final AbstractGameObject[] objects = this.getAllObjects();
        AbstractGameObject o = null;
        for (final AbstractGameObject object : objects) {
            AbstractGameObject instance;
            instance = object.clone();
            if (formatVersion == FormatConstants.MAZE_FORMAT_LATEST) {
                o = instance.readGameObjectV1(reader, UID);
                if (o != null) {
                    return o;
                }
            }
        }
        return null;
    }
}
