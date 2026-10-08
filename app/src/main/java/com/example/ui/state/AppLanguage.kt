package com.example.ui.state

enum class LanguageCode(val displayName: String, val nativeName: String, val flag: String) {
    AMHARIC("Amharic", "አማርኛ", "🇪🇹"),
    ENGLISH("English", "English", "🇬🇧"),
    OROMO("Afaan Oromo", "Afaan Oromoo", "🟢"),
    TIGRINYA("Tigrinya", "ትግርኛ", "🔵")
}

object Strings {
    fun get(key: String, lang: LanguageCode): String {
        val entry = translations[key]
        if (entry != null) {
            return when (lang) {
                LanguageCode.AMHARIC -> entry.amharic
                LanguageCode.ENGLISH -> entry.english
                LanguageCode.OROMO -> entry.oromo
                LanguageCode.TIGRINYA -> entry.tigrinya
            }
        }
        return key
    }

    data class Translation(
        val amharic: String,
        val english: String,
        val oromo: String,
        val tigrinya: String
    )

    private val translations = mapOf(
        // Platform Identity & Branding
        "app_name" to Translation(
            "ኦርቶዶክስ ተዋሕዶ እና ኢኮኖሚ",
            "Ortodox Tewahido and Economy",
            "Ortodoksii Tawaahidoo fi Diinagdee",
            "ኦርቶዶክስ ተዋህዶን ቁጠባን"
        ),
        "tagline" to Translation(
            "ንቁ፣ ብቁና ታታሪ ኦርቶዶክስ — ዘመኑን ለዋጀ አገልግሎት!",
            "Active, Capable and Hardworking Orthodox — Modern Service for a Modern Era.",
            "Ortodoksii Dammaqaa, Gahumsaa fi Hujii Qabaataa — Tajaajila Baraa!",
            "ንቑሕ፣ ብቑዕን ጻዕረኛን ኦርቶዶክስ — ንዘመኑ ዝምጥን ኣገልግሎት!"
        ),
        "sub_tagline" to Translation(
            "የሙያ፣ የንግድ፣ የሥራና የጅምላ ገበያ መረብ",
            "Professional, Business, Jobs & Wholesale Network",
            "Waliigalte Hojii, Daldala, Oomishaa fi Tajaajilaa",
            "ናይ ሞያ፣ ንግዲ፣ ስራሕን ጅምላ ዕዳጋን መርበብ"
        ),

        // Navigation Tabs
        "home" to Translation("ዋና ገጽ", "Home", "Fuula Duraa", "ቀንዲ ገጽ"),
        "jobs" to Translation("ሥራና ሠራተኛ", "Jobs & Careers", "Hojii", "ስራሕ"),
        "marketplace" to Translation("ገበያ", "Marketplace", "Gabaa", "ዕዳጋ"),
        "services" to Translation("አገልግሎት", "Services", "Tajaajila", "ኣገልግሎት"),
        "messages" to Translation("መልእክቶች", "Messages", "Ergaawwan", "መልእኽትታት"),
        "b2b" to Translation("አስመጪና አከፋፋይ (B2B)", "B2B Wholesale", "B2B / Daldala Jumlaa", "ኣምጻእን ዓዳላን (B2B)"),
        "profile" to Translation("መገለጫ", "Profile", "Eenyummaa", "መግለጺ"),
        "notifications" to Translation("ማሳወቂያዎች", "Notifications", "Beeksisoota", "ምልክታታት"),
        "admin" to Translation("አስተዳዳሪ", "Admin", "Bulchaa", "ኣመሓዳሪ"),
        "search" to Translation("ፈልግ", "Search", "Barbaadi", "ድለ"),

        // Search Placeholders (prominently requested in brief)
        "search_hint" to Translation(
            "ሥራ፣ ምርት፣ አገልግሎት ወይም ንግድ ይፈልጉ...",
            "Search jobs, products, services or businesses...",
            "Hojii, oomisha, tajaajila ykn daldala barbaadi...",
            "ስራሕ፣ ፍርያት፣ ኣገልግሎት ወይ ንግዲ ድለ..."
        ),

        // Quick Action Cards
        "find_job" to Translation("ሥራ ፈልግ", "Find a Job", "Hojii Barbaadi", "ስራሕ ድለ"),
        "find_job_desc" to Translation("ክፍት የሥራ ቦታዎችና ቅጥሮች", "Browse open jobs & vacancies", "Hojiiwwan banaa", "ክፉታት ስራሓት"),
        "post_job" to Translation("ሥራ ልጠፍ / ቅጠር", "Hire / Post a Job", "Hojii Baasi / Qacari", "ስራሕ ለጥፍ / ቅጠር"),
        "post_job_desc" to Translation("ባለሙያዎችን በቀላሉ ያግኙ", "Connect with verified talent", "Ogeeyyii qulqullina qaban", "ብቑዓት ሰብ ሞያ ርኸብ"),
        "shop_market" to Translation("የሸቀጥ ገበያ", "Marketplace", "Gabaa Oomishaa", "ዕዳጋ ፍርያት"),
        "shop_market_desc" to Translation("ምርቶችና ባህላዊ አልባሳት", "Products, crafts & items", "Oomishaalee fi uffata", "ፍርያትን ባህላዊ ክዳውንትን"),
        "b2b_wholesale" to Translation("ጅምላና አስመጪ", "B2B & Importers", "B2B fi Galchuu", "ጅምላን ኣምጻእትን"),
        "b2b_wholesale_desc" to Translation("ቀጥታ ከአስመጪዎች ጋር ይገናኙ", "Direct importer trade", "Daldala wal-harkaa", "ቀጥታ ካብ ኣምጻእቲ"),
        "pro_services" to Translation("የሙያ አገልግሎቶች", "Services", "Tajaajila Ogeessaa", "ናይ ሞያ ኣገልግሎት"),
        "pro_services_desc" to Translation("ከታመኑ ባለሙያዎች ጋር ይስሩ", "Hire skilled professionals", "Ogeeyyii amanamoo waliin", "ምስ እሙናት ሰብ ሞያ"),

        // Categories
        "all_categories" to Translation("ሁሉም ዘርፎች", "All Categories", "Gareewwan Hundaa", "ኩሎም ዓውድታት"),
        "cat_tech" to Translation("ቴክኖሎጂና ሶፍትዌር", "Technology & IT", "Teeknoolojii", "ቴክኖሎጂን ሶፍትዌርን"),
        "cat_construction" to Translation("ኮንስትራክሽንና አርክቴክቸር", "Construction & Design", "Ijaarsaa fi Dizaayinii", "ኮንስትራክሽንን ዲዛይንን"),
        "cat_textiles" to Translation("አልባሳትና መንፈሳዊ ጥበባት", "Textiles & Vestments", "Uffata fi Qulqullummaa", "ክዳውንትን ቅዱሳት ንዋያትን"),
        "cat_agri" to Translation("ግብርናና ምግብ", "Agriculture & Food", "Qonnaa fi Nyaata", "ሕርሻን መግብን"),
        "cat_accounting" to Translation("አካውንቲንግና ፋይናንስ", "Accounting & Finance", "Faayinaansii", "ኣካውንቲንግን ፋይናንስን"),
        "cat_education" to Translation("ትምህርትና ስልጠና", "Education & Tutoring", "Barnoota", "ትምህርትን ስልጠናን"),
        "cat_transport" to Translation("ትራንስፖርትና ሎጂስቲክስ", "Transport & Logistics", "Geejjiba", "ትራንስፖርትን ሎጂስቲክስን"),
        "cat_consulting" to Translation("የንግድ ማማከር", "Business Consulting", "Gorsaa Daldalaa", "ምኽሪ ንግዲ"),

        // Section Headers & Actions
        "featured_jobs" to Translation("ተለይተው የቀረቡ የሥራ ክፍት ቦታዎች", "Featured Job Vacancies in Ethiopia", "Hojiiwwan Filataman", "ዝተመርጹ ስራሓት"),
        "featured_products" to Translation("ተወዳጅ የገበያ ምርቶች", "Featured Marketplace Products", "Oomishaalee Filataman", "ዝተመረጹ ናይ ዕዳጋ ፍርያት"),
        "popular_services" to Translation("የታመኑ የሙያ አገልግሎቶች", "Popular Professional Services", "Tajaajiloota Beekamoo", "ፍሉጣት ናይ ሞያ ኣገልግሎታት"),
        "verified_businesses" to Translation("የተረጋገጡ የንግድ ድርጅቶች", "Verified Community Businesses", "Daldaloota Mirkanaa'an", "ዝተረጋገጹ ትካላት ንግዲ"),
        "importer_opps" to Translation("የአስመጪና አከፋፋይ እድሎች (B2B)", "Importer & Wholesale Opportunities", "Carraa Galchuu fi B2B", "ዕድላት ኣምጻእን ዓዳላን"),
        "explore_b2b" to Translation("የጅምላና የአስመጪ እድሎችን ይመልከቱ", "Explore B2B Opportunities", "Carraawwan B2B Barbaadi", "ዕድላት B2B ተመልከት"),
        "view_all" to Translation("ሁሉንም እይ", "View All", "Hunda Ilaali", "ኩሉ ርአ"),
        "apply_now" to Translation("አመልክት", "Apply Now", "Iyyadhu", "ኣመልክት"),
        "submit_proposal" to Translation("ፕሮፖዛል አስገባ", "Submit Proposal", "Iyyata Galchi", "ፕሮፖዛል ኣእቱ"),
        "contact_seller" to Translation("ሻጩን አግኝ", "Contact Seller", "Gurguraa Qunnami", "ንሸያጢ ርኸብ"),
        "request_service" to Translation("አገልግሎት ጠይቅ", "Request Service", "Tajaajila Gaafadhu", "ኣገልግሎት ሕተት"),
        "save" to Translation("አስቀምጥ", "Save", "Olkaawi", "ኣቐምጥ"),
        "saved" to Translation("ተቀምጧል", "Saved", "Olkaa'ameera", "ተቐሚጡ"),
        "share" to Translation("አጋራ", "Share", "Qoodi", "ኣካፍል"),

        // Verification & Trust Section
        "trust_title" to Translation(
            "የተረጋገጠ አባልነትና ደህንነት",
            "Verified Trust & Community Recognition",
            "Mirkaneessa fi Amanamummaa",
            "ዝተረጋገጸ ኣባልነትን ድሕንነትን"
        ),
        "trust_desc" to Translation(
            "በመድረካችን ላይ ያሉ አባላት፣ አሰሪዎችና የንግድ ድርጅቶች በሕጋዊ ሰነድና በአስተዳዳሪው ማረጋገጫ ተረጋግጠው የሚሳተፉበት አስተማማኝ መድረክ ነው።",
            "Members and businesses are verified where applicable to help create a safer and more trusted community.",
            "Miseensonni fi daldaloonni waraqaa ragaatiin mirkanaa'uun wal-dhabdee malee waliin hojjetu.",
            "ኣብዚ መድረኽ ዝርከቡ ኣባላትን ትካላትን ብሕጋዊ ሰነድን ኣመሓዳርን ዝተረጋገጹ ብምዃኖም ውሑስ ማሕበረሰብ ይፈጥሩ።"
        ),
        "get_verified" to Translation("መገለጫዎን ያረጋግጡ", "Get Verified", "Eenyummaa Mirkaneessi", "መግለጺኻ ኣረጋግጽ"),
        "verified_badge" to Translation("የተረጋገጠ አባል", "Verified Member", "Miseensa Mirkanaa'e", "ዝተረጋገጸ ኣባል"),
        "unverified_badge" to Translation("ያልተረጋገጠ", "Unverified", "Kan Hin Mirkanoofne", "ዘይተረጋገጸ"),
        "pending_badge" to Translation("በማረጋገጥ ሂደት", "Pending Verification", "Adeemsa Irra", "ኣብ መስርሕ ምጽራይ"),

        // Platform Metrics
        "stat_members" to Translation("አባላት", "Members", "Miseensota", "ኣባላት"),
        "stat_jobs" to Translation("ክፍት ሥራዎች", "Active Jobs", "Hojiiwwan", "ክፉታት ስራሕ"),
        "stat_products" to Translation("ምርቶች", "Products", "Oomishaalee", "ፍርያት"),
        "stat_deals" to Translation("የጅምላ ውሎች", "B2B Deals", "Walii-galtee", "ናይ ጅምላ ውዕላት"),

        // Dialogs & Selectors
        "select_language" to Translation("ቋንቋ ይምረጡ", "Select Language", "Afaan Filadhu", "ቋንቋ ምረጽ"),
        "select_location" to Translation("ቦታ ይምረጡ", "Select Location", "Bakka Filadhu", "ቦታ ምረጽ"),
        "switch_theme" to Translation("ገጽታ (Light/Dark)", "Toggle Theme", "Bifa Jijjiiri", "ቀይር ገጽታ"),
        "cancel" to Translation("ተመለስ", "Cancel", "Haqi", "ተመለስ"),
        "confirm" to Translation("አረጋግጥ", "Confirm", "Mirkaneessi", "ኣረጋግጽ"),

        // Auth & Accounts
        "sign_in" to Translation("ግባ", "Sign In", "Seeni", "እቶ"),
        "sign_up" to Translation("ተመዝገብ", "Sign Up", "Galmaa'i", "ተመዝገብ"),
        "sign_out" to Translation("ውጣ", "Sign Out", "Bahi", "ውጻእ"),
        "forgot_password" to Translation("የይለፍ ቃል ረሳሁ", "Forgot Password?", "Jecha darbii irraanfadhe", "ይለፍ ቃል ረሲዐ"),
        "email_address" to Translation("ኢሜይል አድራሻ", "Email Address", "Teessoo Imeelii", "ኢመይል ኣድራሻ"),
        "password" to Translation("የይለፍ ቃል", "Password", "Jecha Darbii", "ይለፍ ቃል"),
        "full_name" to Translation("ሙሉ ስም", "Full Name", "Maqaa Guutuu", "ምሉእ ስም"),
        "phone_number" to Translation("ስልክ ቁጥር", "Phone Number", "Lakkoofsa Bilbilaa", "ቁጽሪ ተሌፎን"),
        "user_role" to Translation("የአባልነት ዓይነት / ሚና", "Account Role", "Gahee Hojii", "ዓይነት ኣባልነት"),

        // CV & Resume Builder (Phase 7)
        "cv_resumes" to Translation("ሲቪ እና ሬዙሜ (CV)", "CV & Resumes", "CV fi Seenaa Hojii", "ሲቪን ናይ ስራሕ ታሪኽን"),
        "create_cv" to Translation("አዲስ ሲቪ አዘጋጅ", "Create CV", "CV Haaraa Tolchi", "ሓድሽ ሲቪ ኣዳሉ"),
        "upload_cv" to Translation("ሲቪ ስቀል (Upload PDF)", "Upload CV File", "CV Olkaawi", "ሲቪ ስቐል"),
        "preview_cv" to Translation("ሲቪ እይ", "Preview CV", "CV Ilaali", "ሲቪ ርአ"),
        "download_cv" to Translation("ሲቪ አውርድ", "Download CV", "CV Buusi", "ሲቪ ኣውርድ"),
        "delete_cv" to Translation("ሲቪ ሰርዝ", "Delete CV", "CV Haqi", "ሲቪ ሰርዝ"),
        "personal_summary" to Translation("የሙያ ማጠቃለያ", "Professional Summary", "Gabaasa Ogeessaa", "ናይ ሞያ ሓጺር መግለጺ"),
        "work_experience" to Translation("የሥራ ልምድ", "Work Experience", "Muuxannoo Hojii", "ናይ ስራሕ ተመክሮ"),
        "education_qual" to Translation("የትምህርት ደረጃ", "Education", "Barnoota", "ደረጃ ትምህርቲ"),
        "skills_expertise" to Translation("ክህሎቶች", "Skills & Expertise", "Dandeettii", "ክእለታት"),

        // Documents (Phase 8)
        "documents_title" to Translation("የሙያና የንግድ ሰነዶች", "Documents & Licenses", "Galmeewwan fi Ragaa", "ናይ ሞያን ንግድን ሰነዳት"),
        "upload_doc" to Translation("አዲስ ሰነድ መዝግብ", "Upload Document", "Ragaa Olkaawi", "ሓድሽ ሰነድ መዝግብ"),
        "parish_cert" to Translation("የደብር ምስክር ወረቀት", "Parish Certificate", "Waraqaa Mana Qulqullummaa", "ናይ ደብር ምስክር ወረቐት"),
        "trade_license" to Translation("የንግድ ፈቃድ", "Trade License", "Hayyama Daldalaa", "ናይ ንግዲ ፍቓድ"),

        // Seller Dashboard (Phase 13)
        "seller_dashboard" to Translation("የሻጭ መቆጣጠሪያ ዳሽቦርድ", "Seller Dashboard", "Daashboordii Gurguraa", "ዳሽቦርድ ሸያጢ"),
        "active_listings" to Translation("በገበያ ላይ ያሉ ሸቀጦች", "Active Listings", "Oomishaalee Gabaa Irra Jiran", "ኣብ ዕዳጋ ዘለዉ ፍርያት"),
        "inquiries_received" to Translation("የገዢዎች ጥያቄ", "Buyer Inquiries", "Gaaffiiwwan Bittootaa", "ሕቶታት ዓደግቲ"),
        "seller_analytics" to Translation("የሽያጭ ትንተና", "Sales Analytics", "Xiinxala Gurgurtaa", "ትንተና ሽያጥ"),

        // Favorites (Phase 12)
        "favorites_title" to Translation("የተቀመጡ ምርቶችና ሥራዎች", "Saved & Favorites", "Kanneen Olkaa'aman", "ዝተቐመጡ ፍርያትን ስራሓትን"),
        "no_favorites" to Translation("ምንም የተቀመጠ ነገር የለም", "No saved items yet", "Wanti olkaa'ame hin jiru", "ዝተቐመጠ የለን"),

        // Empty & Error States
        "no_jobs_found" to Translation("ምንም ክፍት ሥራ አልተገኘም", "No jobs found", "Hojiin hin argamne", "ስራሕ ኣይተረኽበን"),
        "no_products_found" to Translation("ምንም ምርት አልተገኘም", "No products available", "Oomishni hin jiru", "ፍርያት ኣይተረኽበን"),
        "no_messages_found" to Translation("ምንም ውይይት የለም", "No conversations yet", "Ergaan hin jiru", "መልእኽቲ የለን"),
        "no_notifications_found" to Translation("አዲስ ማሳወቂያ የለም", "You're all caught up", "Beeksisni hin jiru", "ሓድሽ ምልክታ የለን"),
        "error_generic" to Translation("ችግር ተፈጥሯል፤ እባክዎ እንደገና ይሞክሩ", "Something went wrong. Please retry.", "Dogoggorri uumameera.", "ጸገም ተፈጢሩ፤ ደጊምኩም ፈትኑ።"),
        "retry" to Translation("እንደገና ሞክር", "Retry", "Irra deebi'i", "ደጊምካ ፈትን")
    )
}
