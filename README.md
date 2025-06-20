# WSX

A Burp Suite extension that allows you to export WebSocket history quick and easy, to XML/JSON.

## Usage

1. Navigate to the **Proxy**, then **WebSockets History**
2. Select your desired messages to be exported
3. Right-click and select **Extensions** -> **WSX** -> **"Send to WSX"**
4. Navigate to the **WSX** tab
5. In this tab:
 - Configure export settings:
   - **Client to Server label**: Customize how client-to-server messages are labeled (default: "C-S")
   - **Server to Client label**: Customize how server-to-client messages are labeled (default: "S-C")
   - **Include timestamps**: Check to include message timestamps
   - **Format**: Select either XML or JSON format
   - **Export file**: Use the "Browse..." button to select where to save the file
 - Click **"Export Messages"** to save your WebSocket history

## Building

This extension uses Maven for dependency management. To build:

```bash
mvn clean package
```

The JAR file will be created in the `target/` directory as `WSX-1.x.x.jar`.

## Installation

1. Build the extension or download the JAR file
2. In Burp Suite, go to **Extensions** → **Installed** → **Add**
3. Open WSX-1.x.x.jar 
4. Voilà :3
