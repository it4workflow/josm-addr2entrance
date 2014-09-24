## Description
Simple tool to move addr:* from a way (e.g. building or amenity) to a selected node designated for an [entrance](https://wiki.openstreetmap.org/wiki/Key:entrance).

### Background
Some users prefer to tag the addr scheme to the entrance node and not to the building. If the building already has an addr tag you have to manually copy&paste the tags to the entrance node and then remove it from the building.

### Thanks
Oliver Raupach and his HouseNumberTaggingTool - blueprint for this tool.

## How to use
1. (Create and) select a node in the building outline, which is designated for an entrance
2. Press SHIFT-K
3. Determine the type of entrance (* see Notes below)
4. Check the addr:* values
5. Press OK

### Notes
- The tool only offers entrance values mentioned in [entrance proposal](https://wiki.openstreetmap.org/wiki/Proposed_features/entrance)
- The tool identify an already set [building=entrance](https://wiki.openstreetmap.org/wiki/Tag:building%3Dentrance) and offer to remove it

## Author
Harald Hartmann <osm@haraldhartmann.de>, [OSM-Profile](https://wiki.openstreetmap.org/wiki/User:Haribo)
