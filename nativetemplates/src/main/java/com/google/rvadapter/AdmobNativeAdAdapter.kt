package com.google.rvadapter

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.admob_advanced_native_recyvlerview.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.nativetemplates.NativeTemplateStyle
import com.google.nativetemplates.TemplateView
import java.util.*

/**
 * Created by thuanle on 2/12/17.
 */
class AdmobNativeAdAdapter private constructor(private val mParam: Param) :
    RecyclerViewAdapterWrapper(
        mParam.adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
    init {
        assertConfig()
        setSpanAds()
    }

    private fun assertConfig() {
        if (mParam.gridLayoutManager != null) {
            //if user set span ads
            val nCol = mParam.gridLayoutManager!!.spanCount
            require(mParam.adItemInterval % nCol == 0) {
                String.format(
                    "The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)",
                    mParam.adItemInterval,
                    nCol
                )
            }
        }
    }

    private fun convertAdPosition2OrgPosition(position: Int): Int {
        return position - (position + 1) / (mParam.adItemInterval + 1)
    }

    override fun getItemCount(): Int {
        val realCount = super.getItemCount()
        return realCount + realCount / mParam.adItemInterval
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) {
            TYPE_FB_NATIVE_ADS
        } else super.getItemViewType(convertAdPosition2OrgPosition(position))
    }

    private fun isAdPosition(position: Int): Boolean {
        /*if(position==1|| position==4)return true;*/
        return (position + 1) % (mParam.adItemInterval + 1) == 0
    }

    private fun onBindAdViewHolder(holder: RecyclerView.ViewHolder) {
        val adHolder = holder as AdViewHolder
        if (mParam.forceReloadAdOnBind || !adHolder.loaded) {
            val adLoader = AdLoader.Builder(adHolder.context, mParam.admobNativeId!!)
                .forNativeAd { NativeAd: NativeAd? ->
                    /*NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().withMainBackgroundColor().build();*/

                    //adHolder.template.setStyles(styles);
                    Log.e("admobnative", "loaded")
                    val builder = NativeTemplateStyle.Builder()
                    builder.withPrimaryTextSize(11f)
                    builder.withSecondaryTextSize(10f)
                    builder.withTertiaryTextSize(06f)
                    builder.withCallToActionTextSize(11f)
                    if (mParam.layout == 0) {
                        adHolder.templatesmall.visibility = View.VISIBLE
                        adHolder.templatesmall.setStyles(builder.build())
                        adHolder.templatesmall.setNativeAd(NativeAd)
                    } else if (mParam.layout == 1) {
                        adHolder.templatemedium.visibility = View.VISIBLE
                        adHolder.templatemedium.setStyles(builder.build())
                        adHolder.templatemedium.setNativeAd(NativeAd)
                    } else {
                        adHolder.templatecustom.visibility = View.VISIBLE
                        adHolder.templatecustom.setStyles(builder.build())
                        adHolder.templatecustom.setNativeAd(NativeAd)
                    }
                    adHolder.loaded = true
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.e("admobnative", "error:$loadAdError")
                        adHolder.adContainer.visibility = View.GONE
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindAdViewHolder(holder)
        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position))
        }
    }

    private fun onCreateAdViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val adLayoutOutline = inflater
            .inflate(mParam.itemContainerLayoutRes, parent, false)
        val vg = adLayoutOutline.findViewById<ViewGroup>(mParam.itemContainerId)
        val adLayoutContent = inflater
            .inflate(R.layout.item_admob_native_ad, parent, false) as LinearLayout
        vg.addView(adLayoutContent)
        return AdViewHolder(adLayoutOutline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FB_NATIVE_ADS) {
            onCreateAdViewHolder(parent)
        } else super.onCreateViewHolder(parent, viewType)
    }

    private fun setSpanAds() {
        if (mParam.gridLayoutManager == null) {
            return
        }
        val spl = mParam.gridLayoutManager!!.spanSizeLookup
        mParam.gridLayoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isAdPosition(position)) {
                    spl.getSpanSize(position)
                } else 1
            }
        }
    }

    private class Param {
        var admobNativeId: String? = null
        var adapter: RecyclerView.Adapter<*>? = null
        var adItemInterval = 0
        var forceReloadAdOnBind = false
        var layout = 0

        @LayoutRes
        var itemContainerLayoutRes = 0

        @IdRes
        var itemContainerId = 0
        var gridLayoutManager: GridLayoutManager? = null
    }

    class Builder private constructor(private val mParam: Param) {
        fun adItemInterval(interval: Int): Builder {
            mParam.adItemInterval = interval
            return this
        }

        fun adLayout(@LayoutRes layoutContainerRes: Int, @IdRes itemContainerId: Int): Builder {
            mParam.itemContainerLayoutRes = layoutContainerRes
            mParam.itemContainerId = itemContainerId
            return this
        }

        fun build(): AdmobNativeAdAdapter {
            return AdmobNativeAdAdapter(mParam)
        }

        fun enableSpanRow(layoutManager: GridLayoutManager?): Builder {
            mParam.gridLayoutManager = layoutManager
            return this
        }

        fun adItemIterval(i: Int): Builder {
            mParam.adItemInterval = i
            return this
        }

        fun forceReloadAdOnBind(forced: Boolean): Builder {
            mParam.forceReloadAdOnBind = forced
            return this
        }

        companion object {
            fun with(
                placementId: String?,
                wrapped: RecyclerView.Adapter<*>?,
                layout: String
            ): Builder {
                val param = Param()
                param.admobNativeId = placementId
                param.adapter = wrapped
                if (layout.lowercase(Locale.getDefault()) == "small") {
                    param.layout = 0
                } else if (layout.lowercase(Locale.getDefault()) == "medium") {
                    param.layout = 1
                } else {
                    param.layout = 2
                }

                //default value
                param.adItemInterval = DEFAULT_AD_ITEM_INTERVAL
                param.itemContainerLayoutRes = R.layout.item_admob_native_ad_outline
                param.itemContainerId = R.id.ad_container
                param.forceReloadAdOnBind = true
                return Builder(param)
            }
        }
    }

    private class AdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var templatesmall: TemplateView
        var templatemedium: TemplateView
        var templatecustom: TemplateView
        var adContainer: LinearLayout
        var loaded: Boolean

        init {
            templatesmall = view.findViewById(R.id.my_templatesmall)
            templatecustom = view.findViewById(R.id.my_templatecustom)
            templatemedium = view.findViewById(R.id.my_templatemedium)
            loaded = false
            adContainer = view.findViewById<View>(R.id.native_ad_container) as LinearLayout
        }

        val context: Context
            get() = adContainer.context
    }

    companion object {
        private const val TYPE_FB_NATIVE_ADS = 900
        private const val DEFAULT_AD_ITEM_INTERVAL = 4
        fun isValidPhoneNumber(target: CharSequence): Boolean {
            return if (target.length != 10) {
                false
            } else {
                Patterns.PHONE.matcher(target).matches()
            }
        }
    }
}