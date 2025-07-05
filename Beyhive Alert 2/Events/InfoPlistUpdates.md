# Info.plist Updates Required

Add the following keys to your Info.plist file to ensure proper network access:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

This allows the app to make network requests to your backend server.
