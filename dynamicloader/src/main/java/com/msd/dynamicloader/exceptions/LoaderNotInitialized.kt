package com.msd.dynamicloader.exceptions

import java.lang.Exception

class LoaderNotInitialized : Exception("Loader core has not being initialized for this activity. Please, call init(...) in onCreate method or extend" +
        "LoaderAppCompatActivity")