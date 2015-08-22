# Kintone output plugin for Embulk

TODO: Developping now.
TODO: Write short description here and build.gradle file.

Kintone output plugins for Embulk loads records to databases using JDBC drivers.
Kintone output plugins for Embulk loads records to kintone( https://www.kintone.com/ )

## Overview

* **Plugin type**: output
* **Load all or nothing**: no
* **Resume supported**: no
* **Cleanup supported**: yes

## Configuration

- **property1**: description (string, required)
- **property2**: description (integer, default: default-value)

## Example

```yaml
out:
  type: kintone
  property1: example1
  property2: example2
```


## Build

```
$ ./gradlew gem
```
