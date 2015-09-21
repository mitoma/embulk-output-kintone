# Kintone output plugin for Embulk

Kintone output plugins for Embulk loads records to kintone. ( https://www.kintone.com/ )

TODO: Developping now.

DONE: NUMBER, SINGLE_LINE_TEXT, DATE, DATETIME support.
DONE: insert mode.

TODO: documentation, testing.
TODO: update mode.
TODO: upsert mode.
TODO: multi selection, checkbox support.
TODO: resume, retry.

## Overview

* **Plugin type**: output
* **Load all or nothing**: no
* **Resume supported**: no
* **Cleanup supported**: yes(?)

## Configuration

- **type**: kintone (string, required)
- **domain**: your kintone domain name. (string, required)
- **app_id**: your kintone app id. (integer, required)
- **auth_type**: authentication type. (string, api_token or basic (yet not supported))
- **api_token**: application api token. (string)
- **mode**: loding mode. (string, insert)
- **column_options**: load setting. key is embulk's column name. value is map of kintone column(field_code, type)

## Example

```yaml
out:
  type: kintone
  domain: [your domain name]
  app_id: [your app id]
  auth_type: api_token
  api_token: [your api token]
  mode: insert
  column_options:
    column_name1: {field_code: "num",      type: "NUMBER"           }
    column_name2: {field_code: "string",   type: "SINGLE_LINE_TEXT" }
    column_name3: {field_code: "date",     type: "DATE"             }
    column_name3: {field_code: "datetime", type: "DATETIME"         }
```

## Build

```
$ ./gradlew gem
```
