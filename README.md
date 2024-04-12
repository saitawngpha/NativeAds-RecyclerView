## NativeAds RecyclerView Library
>Android library for add Naive Ads into RecyclerView list items.

## Update
- Updated to the latest version of Admob SDK
- Supported on Android SDK 30, 31, 32, 33, 34
- Updated RVAdapter to Kotlin

## For developer
To make your app can earn more revengue ` by Showing Native Ads into RecyclerView list items `. Please check more details from below.

## How to use NaiveAds RecyclerView Library
Step 1. Add the JitPack repository to your build file 
  ```
  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  If you are using Android Studio Bumble bee or latest version, add JitPack repository to `settings.gradle` file.
  ```
  dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' } // add this line
    }
}
```
  
  
Step 2. Add the dependency
  

	dependencies {
	        implementation 'com.github.saitawngpha:NativeAds-RecyclerView:1.0.2'
	}


Step 3. Add below code to setup your Adapter by Kotlin.
  

        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            val admobNativeAdAdapter: AdmobNativeAdAdapter = AdmobNativeAdAdapter.Builder
                .with(
                    "ca-app-pub-3940256099942544/2247696110",  //Create a native ad id from admob console
                    myAdapter,  //The adapter you would normally set to your recyClerView
                    "medium" //Set it with "small","medium" or "custom"
                )
                .adItemIterval(2) //native ad repeating interval in the recyclerview
                .build()
            recyclerView.adapter = admobNativeAdAdapter
           
 Add below code to setup your Adapter by Java.
  

      AdmobNativeAdAdapter admobNativeAdAdapter=AdmobNativeAdAdapter.Builder
	      .with(
		      "ca-app-pub-3940256099942544/2247696110",//Create a native ad id from admob console
		      myAdapter,//The adapter you would normally set to your recyClerView
		      "medium"//Set it with "small","medium" or "custom"
		      )
	    .adItemIterval(2)//native ad repeating interval in the recyclerview
	    .build();
    recyclerView.setAdapter(admobNativeAdAdapter);//set your RecyclerView adapter with the admobNativeAdAdapter

## Demo
Native ads Small Layout |  Native ads Medium Layout
:-------------------------:|:-------------------------:
<img src="1.png" width="250px"/>  |  <img src="2.png" width="250px"/> 

## Reference Project
![Special thanks to daoibrahim](https://github.com/daoibrahim/AdmobAdvancedNativeRecyclerview)


### Donate Me
| SHIBU | BTC | ETH | DOGE |
| :----------: | :-----------: | :-----------: | :-----------: |
| <img src="https://github.com/saitawngpha/saitawngpha/raw/main/donate/shib.JPG" width="250px"/> | <img src="https://github.com/saitawngpha/saitawngpha/raw/main/donate/btc.JPG" width="250px"/> | <img src="https://github.com/saitawngpha/saitawngpha/raw/main/donate/eth.JPG" width="250px"/> | <img src="https://github.com/saitawngpha/saitawngpha/raw/main/donate/doge.JPG" width="250px"/> |
