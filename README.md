# Au10tix SDK Implementation Example - Passport flow

This project is implements a basic passport verification flow using Au10tix's android SDK.

## Table of Contents
- [Overview](#overview)
- [Usage](#usage)
    - [Add passport library](#add-passport-library)
    - [Artifactory password](#artifactory-password)
    - [JWT token](#jwt-token)
    

## Overview
Verified, compliant and fraud-free onboarding results in eight seconds (or less). By the time you read this sentence, AU10TIX will have converted countless human smiles, identity documents and data points into authenticated, all-access passes to your products, services and experiences.

This example application presents an implementation suggestion for a basic passport verification flow using Au10tix's android SDK.

## Usage

To use this sample, edit the sample files according to the following steps.

### Add passport library
Add the passport .jar file to the libs folder.

If you don't have the file, contact support.

### Artifactory password
1. Contact support for a password to get the artifacts you need. 
1. Modify the project's settings.gradle after you receive the password. 

```
password "***CONTACT_SUPPORT_FOR_PASSWORD***"
```

### JWT token
The SDK is prepared using the JWT token produced by the client's server.
Acquire the JWT token and modify Au10NetworkHelper.java to correctly include the values attained from your server.

On MainFragment class
```java
        private const val AU10TIX_BEARER_TOKEN = "xxx.xxx.xxx"
```

