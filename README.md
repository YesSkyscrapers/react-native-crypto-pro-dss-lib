# react-native-crypto-pro-dss-lib

React-Native module for using CryptoPro Dss SDKs.

## Installation

Before installation u should create `dss` folder inside your root project.
Add here certs.json and SDKStyles.json files. 
Inside `dss` folder create `fonts` folder and put your fonts which used in style file.
Examples of files you can find `scripts/example_dss_folder`.
After this steps, you can install lib, which will be postinstall your assets files.

```bash
yarn add react-native-crypto-pro-dss-lib
cd ios
pod install
```

You can add `"update-dss-assets": "node node_modules/react-native-crypto-pro-dss-lib/scripts/installDssAssets.js -fromRootProject"` inside your package.json file for manual assets installation.

# iOS

Open XCode and add inside your root project `Resources` folder. Add here all files(not folders), ru.lproj and en.jproj  from `node_modules/react-native-crypto-pro-dss-lib/Frameworks/CPROCSP.framework/Resources` folder. Add here your fonts too, which used in styles file.

## Usage

Before calling methods u should call 

```js
import CryptoProDssLib from 'react-native-crypto-pro-dss-lib';

CryptoProDssLib.updateStyles().then(() => {
    CryptoProDssLib.sdkInitialization().then(() => {
        console.log('done')
    })
})
```

After initialization u can get sdk users

```js
import CryptoProDssLib from 'react-native-crypto-pro-dss-lib';

CryptoProDssLib.getUsers().then((users) => {
    console.log('users', users)
})
```

Use this method to get operations for signing

```js
import CryptoProDssLib from 'react-native-crypto-pro-dss-lib';

CryptoProDssLib.getOperations().then((operations) => {
    console.log('operations', operations)
})
```

Use this method to get operations for signing

```js
import CryptoProDssLib from 'react-native-crypto-pro-dss-lib';

CryptoProDssLib.getOperations().then((operations) => {
    console.log('operations', operations)
})
```

Use this method to init new use via QR

```js
import CryptoProDssLib from 'react-native-crypto-pro-dss-lib';

CryptoProDssLib.initViaQr().then(() => {
    let kid = ... // get kid from getUsers method
    CryptoProDssLib.continueInitViaQr(kid).then(() => {
        console.log('done')
    })
})
```

Use this method to sign doc

```js
import CryptoProDssLib from 'react-native-crypto-pro-dss-lib';

let kid = ... // get kid from getUsers method
let transactionId = ... // get transactionId from getOperations method
CryptoProDssLib.signMT(transactionId, kid).then((result) => {
    // u can use result for check user actions. Did he sign, or reject, or cancel
    CryptoProDssLib.deferredRequest(kid).then(() => {
        console.log('done')
    })
})
```



