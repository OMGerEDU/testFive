# testFive

This project uses a mirrored Maven repository to avoid outages at the default Maven Central endpoint. Both dependency and plugin resolution are configured to use `https://repo1.maven.org/maven2`.

## Setup

1. Populate the local Maven cache so builds can run offline:
   ```bash
   mvn -q dependency:go-offline
   ```
2. Run the test suite offline or behind a proxy:
   ```bash
   mvn -q -o test
   ```

These steps ensure repeatable builds without requiring external network access after the initial dependency download.
