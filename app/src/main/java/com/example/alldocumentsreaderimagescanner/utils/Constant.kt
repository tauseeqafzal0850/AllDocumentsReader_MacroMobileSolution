package com.example.alldocumentsreaderimagescanner.utils

object Constant {

    var isCollumNull = false
    var pdfSizeCount:Int? = 0
    var excelSizeCount = 0
    var wordSizeCount = 0
    var pptSizeCount = 0
    var txtSizeCount = 0

    const val PRIVACY_POLICY_KEY = "key_privacy_policy"
    const val PERMISSION_SHOW_KEY = "permission_show_key"
    var IS_CONFIG_FETCH_SUCCESS = false
    var IS_REMOTE_DATA_SAVE_SUCCESS = false
    const val NO_AD: String="0"
    const val AD_ADMOB = "1"
    const val AD_MAX = "2"
    var SHOW_AD = false
    /**
     *  Ads Controls Constants
     *  */

    /** Admob Main */
    var KEY_ADMOB_MAIN_INTERSTITIAL = false

    @JvmField
    var KEY_ADMOB_MAIN_BANNER = false

    @JvmField
    var KEY_ADMOB_MAIN_NATIVE = false

    /** Applovin Main */
    var APPLOVIN_MAIN_INTERSTITIAL_ENABLED = false

    @JvmField
    var APPLOVIN_MAIN_BANNER_ENABLED = false

    @JvmField
    var APPLOVIN_MAIN_NATIVE_ENABLED = false

    /**
     * Firebase Remote Config Params
     * */

    /** Admob Ad Ids */
    const val ADMOB_APP_OPEN_LOADING_KEY = "admob_app_open_loading_key"
    const val ADMOB_INTERSTITAL_LOADING_KEY = "admob_interstital_loading_key"
    const val ADMOB_BANNER_AD_KEY = "admob_banner_key"
    const val ADMOB_INTERSTITIAL_AD_KEY = "admob_interstitial_key"
    const val ADMOB_NATIVE_AD_KEY = "admob_native_key"

    /** Applovin Ad Ids */
    const val KEY_APPLOVIN_BANNER_AD_ID = "key_applovin_banner_ad_id"
    const val KEY_APPLOVIN_NATIVE_AD_ID = "key_applovin_native_ad_id"
    const val KEY_APPLOVIN_INTERSTITIAL_AD_ID = "key_applovin_interstitial_ad_id"
    const val KEY_APPLOVIN_INTERSTITIAL_LOADING_AD_ID = "key_applovin_interstitial_loading_ad_id"

    /** Admob Main Controls */
    const val KEY_ADMOB_MAIN_INTERSTITIAL_ENABLED = "key_admob_main_interstitial_enabled"
    const val KEY_ADMOB_MAIN_BANNER_ENABLED = "key_admob_main_banner_enabled"

    /** Applovin Main Controls */
    const val KEY_APPLOVIN_MAIN_INTERSTITIAL_ENABLED = "key_applovin_main_interstitial_enabled"
    const val KEY_APPLOVIN_MAIN_BANNER_ENABLED = "key_applovin_main_banner_enabled"




    /**
     *  Check response Interstitial Ad from firebase remote config
     * */
    const val ALL_FILES_INTERSTITIALS = "all_files_interstitials"
    const val PDF_FILES_INTERSTITIALS = "pdf_files_interstitials"
    const val WORD_FILES_INTERSTITIALS = "word_files_interstitials"
    const val EXCEL_FILES_INTERSTITIALS = "excel_files_interstitials"
    const val DOCUMENTS_SCANNER_INTERSTITIALS = "documents_scanner_interstitials"
    const val PASSPORT_SCANNER_INTERSTITIALS = "passport_scanner_interstitials"
    const val QR_GENERATOR_INTERSTITIALS = "qr_generator_interstitials"
    const val NOTEPAD_INTERSTITIALS = "notepad_interstitials"
    const val PDF_MERGE_INTERSTITIALS = "pdf_merge_interstitials"
    const val IMAGE_TO_PDF_INTERSTITIALS = "image_to_pdf_interstitials"
    const val TEXT_TO_PDF_INTERSTITIALS = "text_to_pdf_interstitials"
    const val EXCEL_TO_PDF_INTERSTITIALS = "excel_to_pdf_interstitials"

    /**
     *  native Ad
     * */
    const val HOME_SCREEN_NATIVE = "home_screen_native"
    /**
     *  banner Ad
     * */
    const val HOME_SCREEN_BANNER = "home_screen_banner"
    const val ALL_FILES_BANNER = "all_files_banner"
    const val IMAGE_TO_PDF_BANNER = "image_to_pdf_banner"
    const val TEXT_TO_PDF_BANNER = "text_to_pdf_banner"
    const val EXCEL_TO_PDF_BANNER = "excel_to_pdf_banner"
    const val PDF_FILES_BANNER = "pdf_files_banner"
    const val WORD_FILES_BANNER = "word_files_banner"
    const val EXCEL_FILES_BANNER = "excel_files_banner"
    const val PPT_FILES_BANNER = "ppt_files_banner"
    const val TEXT_FILES_BANNER = "text_files_banner"
    const val DOCUMENTS_SCANNER_BANNER = "documents_scanner_banner"
    const val ID_CARD_SCANNER_BANNER = "id_card_scanner_banner"
    const val PASSPORT_SCANNER_BANNER = "passport_scanner_banner"
    const val QR_GENERATOR_BANNER = "qr_generator_banner"
    const val NOTEPAD_BANNER = "notepad_banner"
    const val PDF_MERGE_BANNER = "pdf_merge_banner"



    /**
     *  Contains 0 OR 1 OR 2 (0 for disabled, 1 for admob, 2 for applovin)
     * */

    /**
     *  Interstitial Ad
     * */
    var CHECK_ALL_FILES_INTERSTITIALS = "0"
    var CHECK_PDF_FILES_INTERSTITIALS = "0"
    var CHECK_WORD_FILES_INTERSTITIALS = "0"
    var CHECK_EXCEL_FILES_INTERSTITIALS = "0"
    var CHECK_DOCUMENTS_SCANNER_INTERSTITIALS = "0"
    var CHECK_PASSPORT_SCANNER_INTERSTITIALS = "0"
    var CHECK_QR_GENERATOR_INTERSTITIALS = "0"
    var CHECK_NOTEPAD_INTERSTITIALS = "0"
    var CHECK_PDF_MERGE_INTERSTITIALS = "0"
    var CHECK_IMAGE_TO_PDF_INTERSTITIALS = "0"
    var CHECK_TEXT_TO_PDF_INTERSTITIALS = "0"
    var CHECK_EXCEL_TO_PDF_INTERSTITIALS = "0"
    /**
     *  native Ad
     * */
    var CHECK_HOME_SCREEN_NATIVE = "0"




    /**
     *  banner Ad
     * */
    var CHECK_HOME_SCREEN_BANNER = "0"
    var CHECK_ALL_FILES_BANNER = "0"
    var CHECK_IMAGE_TO_PDF_BANNER = "0"
    var CHECK_TEXT_TO_PDF_BANNER = "0"
    var CHECK_EXCEL_TO_PDF_BANNER = "0"
    var CHECK_PDF_FILES_BANNER = "0"
    var CHECK_WORD_FILES_BANNER = "0"
    var CHECK_EXCEL_FILES_BANNER = "0"
    var CHECK_PPT_FILES_BANNER = "0"
    var CHECK_TEXT_FILES_BANNER = "0"
    var CHECK_DOCUMENTS_SCANNER_BANNER = "0"
    var CHECK_ID_CARD_SCANNER_BANNER = "0"
    var CHECK_PASSPORT_SCANNER_BANNER = "0"
    var CHECK_QR_GENERATOR_BANNER = "0"
    var CHECK_NOTEPAD_BANNER = "0"
    var CHECK_PDF_MERGE_BANNER = "0"


    /**
     * splash Ad key
     * */
    var APPLOVIN_LOADING_INTERSTITIAL_AD_ID = ""
    var ADMOB_LOADING_APP_OPEN_AD_ID = ""
    var ADMOB_LOADING_INTERSTITIAL_AD_ID = ""
}