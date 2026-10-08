package com.example.data.repository

import com.example.data.model.*

object SeedData {
    val users = listOf(
        UserEntity(
            id = "usr_admin",
            email = "admin@tewahidonet.et",
            passwordHash = "admin123",
            fullName = "ዲ/ን አማኑኤል ተስፋዬ (Dn. Amanuel)",
            username = "amanuel_admin",
            primaryRole = UserRole.ADMIN.name,
            secondaryRoles = "${UserRole.BUSINESS.name},${UserRole.SERVICE_PROVIDER.name}",
            bio = "የተዋሕዶ መረብ ዋና አስተዳዳሪ። በአስተዳደር እና በዲጂታል ቴክኖሎጂ የማኅበረሰባችንን ኢኮኖሚያዊ ትስስር ማጠናከር።",
            location = "Addis Ababa, Bole",
            phone = "+251911223344",
            profession = "Systems Architect & Platform Administrator",
            businessName = "Tewahido Net HQ",
            businessCategory = "Information Technology",
            verificationStatus = VerificationStatus.VERIFIED.name,
            rating = 5.0f,
            reviewCount = 28,
            joinedDate = "2025-01-10"
        ),
        UserEntity(
            id = "usr_emp_selam",
            email = "contact@selamtech.et",
            passwordHash = "selam123",
            fullName = "ኢንጅነር ሚካኤል ሀብቴ (Eng. Michael)",
            username = "michael_selamtech",
            primaryRole = UserRole.EMPLOYER.name,
            secondaryRoles = "${UserRole.BUSINESS.name},${UserRole.SERVICE_PROVIDER.name}",
            bio = "ሰላም ቴክኖሎጂ ሶሉሽንስ - ጥራት ያላቸው የሶፍትዌርና የኔትወርክ መፍትሔዎችን ለሀገር በቀል ድርጅቶች የምናቀርብ ተቋም ነን።",
            location = "Addis Ababa, Kazanchis",
            phone = "+251911556677",
            profession = "Managing Director & Chief Engineer",
            businessName = "Selam Technology Solutions PLC",
            businessCategory = "Technology & Engineering",
            verificationStatus = VerificationStatus.VERIFIED.name,
            rating = 4.9f,
            reviewCount = 19,
            joinedDate = "2025-02-14"
        ),
        UserEntity(
            id = "usr_seeker_marta",
            email = "marta.wolde@gmail.com",
            passwordHash = "marta123",
            fullName = "ማርታ ወልደሚካኤል (Marta W.)",
            username = "marta_finance",
            primaryRole = UserRole.JOB_SEEKER.name,
            secondaryRoles = "${UserRole.SERVICE_PROVIDER.name}",
            bio = "ከአዲስ አበባ ዩኒቨርሲቲ በአካውንቲንግና ፋይናንስ የተመረቅኩ። የ8 ዓመት የባንክና የኦዲት ልምድ ያለኝ ከፍተኛ የሂሳብ ባለሙያ።",
            location = "Addis Ababa, CMC",
            phone = "+251922334455",
            profession = "Senior Financial Auditor & CPA",
            businessName = "",
            businessCategory = "Accounting & Finance",
            verificationStatus = VerificationStatus.VERIFIED.name,
            rating = 5.0f,
            reviewCount = 12,
            joinedDate = "2025-03-01"
        ),
        UserEntity(
            id = "usr_seller_lalibela",
            email = "lalibela.art@gmail.com",
            passwordHash = "lalibela123",
            fullName = "ወ/ሮ ሰብለወንጌል ተሾመ (Seblewengel T.)",
            username = "lalibela_heritage",
            primaryRole = UserRole.SELLER.name,
            secondaryRoles = "${UserRole.BUSINESS.name},${UserRole.BUYER.name}",
            bio = "በእጅ የተሰፉ ጥበቦች፣ የካህናትና የዲያቆናት አልባሳት፣ መንፈሳዊ ቅርሶች እና ጥራት ያላቸው የሀገር ባህል ልብሶች አዘጋጅና ሻጭ።",
            location = "Addis Ababa, Shola Market",
            phone = "+251933445566",
            profession = "Artisan & Traditional Garment Designer",
            businessName = "Lalibela Sacred Vestments & Culture",
            businessCategory = "Textiles & Spiritual Arts",
            verificationStatus = VerificationStatus.VERIFIED.name,
            rating = 4.95f,
            reviewCount = 42,
            joinedDate = "2025-01-22"
        ),
        UserEntity(
            id = "usr_imp_yared",
            email = "yared@tewahidoglobal.com",
            passwordHash = "yared123",
            fullName = "አቶ ያሬድ በርሀነ (Yared Berhane)",
            username = "yared_importer",
            primaryRole = UserRole.IMPORTER.name,
            secondaryRoles = "${UserRole.BUSINESS.name},${UserRole.SELLER.name}",
            bio = "ከቱርክና ከዱባይ ጥራት ያላቸው የኢንዱስትሪ ማሸጊያዎች፣ የህክምና መሣሪያዎችና የኮንስትራክሽን ግብአቶችን በጅምላ እናስመጣለን።",
            location = "Addis Ababa, Gotera Industrial Zone",
            phone = "+251944556677",
            profession = "Licensed International Importer",
            businessName = "Tewahido Global Import-Export",
            businessCategory = "Wholesale & B2B Logistics",
            verificationStatus = VerificationStatus.VERIFIED.name,
            rating = 4.88f,
            reviewCount = 31,
            joinedDate = "2025-02-05"
        ),
        UserEntity(
            id = "usr_dist_admas",
            email = "admas.dist@ethionet.et",
            passwordHash = "admas123",
            fullName = "ወ/ሪት ራሔል ግርማ (Rahel Girma)",
            username = "admas_distribution",
            primaryRole = UserRole.DISTRIBUTOR.name,
            secondaryRoles = "${UserRole.BUYER.name},${UserRole.BUSINESS.name}",
            bio = "በሀገር አቀፍ ደረጃ በሁሉም ክልሎች ሰፊ የመጋዘንና የስርጭት መረብ ያለን የጅምላ አከፋፋይ ድርጅት።",
            location = "Addis Ababa, Kality Depot",
            phone = "+251955667788",
            profession = "Regional Supply Chain Director",
            businessName = "Admas Distribution Network",
            businessCategory = "Wholesale Distribution & FMCG",
            verificationStatus = VerificationStatus.VERIFIED.name,
            rating = 4.85f,
            reviewCount = 15,
            joinedDate = "2025-03-12"
        ),
        UserEntity(
            id = "usr_svc_dawit",
            email = "dawit.arch@gmail.com",
            passwordHash = "dawit123",
            fullName = "መሐንዲስ ዳዊት ገብሬ (Eng. Dawit)",
            username = "dawit_architect",
            primaryRole = UserRole.SERVICE_PROVIDER.name,
            secondaryRoles = "${UserRole.BUSINESS.name}",
            bio = "የ12 ዓመት ልምድ ያለው የቤተክርስቲያን እና የመኖሪያ ህንፃዎች አርክቴክትና የኮንስትራክሽን አማካሪ።",
            location = "Addis Ababa, Megenagna",
            phone = "+251966778899",
            profession = "Senior Architect & Structural Consultant",
            businessName = "Betelhem Architectural Studio",
            businessCategory = "Architecture & Construction",
            verificationStatus = VerificationStatus.VERIFIED.name,
            rating = 5.0f,
            reviewCount = 23,
            joinedDate = "2025-01-18"
        ),
        UserEntity(
            id = "usr_new_yonas",
            email = "yonas.tadesse@gmail.com",
            passwordHash = "yonas123",
            fullName = "ዮናስ ታደሰ (Yonas Tadesse)",
            username = "yonas_mobile",
            primaryRole = UserRole.JOB_SEEKER.name,
            secondaryRoles = "${UserRole.BUYER.name}",
            bio = "በሞባይልና ድህረ-ገፅ ግንባታ የተሰማራሁ ጀማሪ ገንቢ። የስራ እድሎችንና ፕሮጀክቶችን እፈልጋለሁ።",
            location = "Addis Ababa, Saris",
            phone = "+251977889900",
            profession = "Junior Software Developer",
            businessName = "",
            businessCategory = "Technology",
            verificationStatus = VerificationStatus.PENDING.name,
            verificationDocNote = "የቀበሌ መታወቂያ እና የደብር ምስክር ወረቀት አያይዣለሁ።",
            rating = 4.7f,
            reviewCount = 2,
            joinedDate = "2026-02-01"
        )
    )

    val jobs = listOf(
        JobEntity(
            id = "job_eth_1",
            employerId = "usr_seller_lalibela",
            employerName = "ማርታ ኃይሌ (ቅዱስ ጊዮርጊስ የጨርቃጨርቅ ንግድ)",
            employerVerified = true,
            title = "Retail Shop Clerk & Cashier (የሱቅ ሽያጭና ገንዘብ ያዥ)",
            amharicTitle = "የሱቅ ሽያጭና ገንዘብ ያዥ ሠራተኛ — መርካቶ",
            category = "የሱቅና ሽያጭ (Retail)",
            location = "አዲስ አበባ፣ መርካቶ (Merkato)",
            salaryRange = "12,000 - 15,000 ETB / ወር",
            employmentType = EmploymentType.FULL_TIME.name,
            experienceLevel = "1+ ዓመት ልምድ",
            description = "በመርካቶ የጨርቃጨርቅና የባህል ልብሶች መደብራችን ውስጥ ታማኝ፣ የደንበኛ አያያዝ ችሎታ ያላት፣ ደረሰኝ በትክክል የምትቆርጥና የሽያጭ መዝገብ የምትይዝ ክርስቲያን የሱቅ ሠራተኛ በቋሚነት እንቀጥራለን። የስራ ሰዓት፡ ሰኞ - ቅዳሜ።",
            deadline = "2026-12-30",
            projectType = "FULL_TIME",
            budgetAmount = 14000.0,
            deliveryDays = 30,
            skillsRequired = "ታማኝነት፣ የደንበኛ አያያዝ፣ የሂሳብ መዝገብ፣ መሰረታዊ ኮምፒውተር",
            proposalsCount = 6,
            employerPhone = "+251911456789"
        ),
        JobEntity(
            id = "job_eth_2",
            employerId = "usr_imp_yared",
            employerName = "ተዋሕዶ ትራንስፖርትና ሎጂስቲክስ (ያሬድ ታደሰ)",
            employerVerified = true,
            title = "Commercial Light Truck Driver (የመኪና አሽከርካሪ / ሾፌር)",
            amharicTitle = "የመኪና አሽከርካሪ (ህዝብ 1 ወይም ደረቅ 1) — ቃሊቲ",
            category = "መኪና አሽከርካሪ (Drivers)",
            location = "አዲስ አበባ፣ ቃሊቲ / ቦሌ (Kality)",
            salaryRange = "16,000 - 20,000 ETB / ወር",
            employmentType = EmploymentType.FULL_TIME.name,
            experienceLevel = "2+ ዓመት ልምድ",
            description = "ለአስመጪ ድርጅታችን የሸቀጣሸቀጥ እቃዎች ማከፋፈያ የሚሆን አይሱዙ ወይም ኤፍኤስአር (FSR) መኪና በብቃት የሚያሽከረክር፣ ሕጋዊ መንጃ ፈቃድ ያለው፣ ሥነ-ምግባር ያለውና ታማኝ አሽከርካሪ በቋሚነት እንፈልጋለን።",
            deadline = "2026-12-15",
            projectType = "FULL_TIME",
            budgetAmount = 18000.0,
            deliveryDays = 30,
            skillsRequired = "መንጃ ፈቃድ ደረቅ 1፣ የመኪና ጥገና እውቀት፣ የታማኝነት ዋስ",
            proposalsCount = 9,
            employerPhone = "+251922334455"
        ),
        JobEntity(
            id = "job_eth_3",
            employerId = "usr_svc_dawit",
            employerName = "ኢንጂነር ዳዊት ገብሬ (የቤተክርስቲያን ህንፃ ስራ)",
            employerVerified = true,
            title = "Master Carpenter & Wood Carver (የእንጨት ጥበብና አናፂ)",
            amharicTitle = "የእንጨት ጥበብ፣ በር፣ መስኮትና ፈርኒቸር ሰሪ — ቦሌ",
            category = "እደ-ጥበብና ግንባታ (Trades)",
            location = "አዲስ አበባ፣ ቦሌ ቡልቡላ (Bole Bulbula)",
            salaryRange = "45,000 ETB (በውል/ፕሮጀክት)",
            employmentType = EmploymentType.CONTRACT.name,
            experienceLevel = "ከፍተኛ ባለሙያ",
            description = "ለአብነት ትምህርት ቤትና ለደብር አዳራሽ የሚሆኑ ዘመናዊ የእንጨት በሮች፣ ጠረጴዛዎችና የተቀረጹ መስኮቶች በውል ስምምነት በጥራት አጠናቆ የሚያስረክብ ልምድ ያለው አናፂ እንፈልጋለን። እቃዎች በሙሉ ይቀርባሉ።",
            deadline = "2026-12-25",
            projectType = "CONTRACT",
            budgetAmount = 45000.0,
            deliveryDays = 21,
            skillsRequired = "የእንጨት ቅርጻቅርፅ፣ አናፂነት፣ ካቢኔት ስራ፣ ፊኒሺንግ",
            proposalsCount = 4,
            employerPhone = "+251933445566"
        ),
        JobEntity(
            id = "job_eth_4",
            employerId = "usr_emp_selam",
            employerName = "ሰላም ኬክና ካፌ (Selam Pastry & Cafe)",
            employerVerified = true,
            title = "Pastry Chef & Baker (የዳቦና የኬክ ጋጋሪ)",
            amharicTitle = "የዳቦና የኬክ ጋጋሪ / ሼፍ — 22 ማዞሪያ",
            category = "ምግብ ዝግጅት (Hospitality)",
            location = "አዲስ አበባ፣ 22 ማዞሪያ (22 Mazoria)",
            salaryRange = "15,000 - 18,000 ETB / ወር",
            employmentType = EmploymentType.FULL_TIME.name,
            experienceLevel = "2+ ዓመት ልምድ",
            description = "የፆምና የፍስክ ኬኮችን፣ ፒዛ፣ ኩኪስና ልዩ ልዩ የዳቦ ምርቶችን በከፍተኛ ጥራት የሚያዘጋጅ፣ ንጽሕናን አጥብቆ የሚጠብቅና ከቡድን ጋር ተግባብቶ የሚሰራ ሼፍ እንፈልጋለን።",
            deadline = "2026-12-20",
            projectType = "FULL_TIME",
            budgetAmount = 16500.0,
            deliveryDays = 30,
            skillsRequired = "የኬክ ጋጋሪነት፣ የንጽሕና አጠባበቅ፣ የቡድን ስራ፣ የፆም ምግቦች ዝግጅት",
            proposalsCount = 7,
            employerPhone = "+251944556677"
        ),
        JobEntity(
            id = "job_eth_5",
            employerId = "usr_admin",
            employerName = "ወ/ሮ ጽዮን ካሳሁን",
            employerVerified = true,
            title = "Child Caregiver & Home Assistant (የሕፃናት ሞግዚትና የቤት እመቤት)",
            amharicTitle = "የሕፃናት ሞግዚትና የቤት እመቤት — ሲኤምሲ",
            category = "ክብካቤ (Home & Care)",
            location = "አዲስ አበባ፣ ሲኤምሲ (CMC Michael)",
            salaryRange = "9,000 - 11,000 ETB / ወር",
            employmentType = EmploymentType.FULL_TIME.name,
            experienceLevel = "ልምድ ያላት",
            description = "ለሁለት ህፃናት ፍቅርና ጥንቃቄ የምትሰጥ፣ ምግብ የምታዘጋጅ፣ ቤት የምትንከባከብ፣ ክርስቲያናዊ ስነ-ምግባር ያላትና አብራ የምታድር ታማኝ ሞግዚት እንፈልጋለን። ሙሉ መኖሪያና ምግብ በቤተሰቡ ይሸፈናል።",
            deadline = "2026-12-18",
            projectType = "FULL_TIME",
            budgetAmount = 10000.0,
            deliveryDays = 30,
            skillsRequired = "የህፃናት አያያዝ፣ የቤት አያያዝ፣ ምግብ ማብሰል፣ ታማኝነት",
            proposalsCount = 11,
            employerPhone = "+251955667788"
        ),
        JobEntity(
            id = "job_eth_6",
            employerId = "usr_imp_yared",
            employerName = "ዳዊት ታከለ (የኤሌክትሪክ ስራ ተቋራጭ)",
            employerVerified = true,
            title = "Building Electrician Assistant (የኤሌክትሪክ መስመር ዝርጋታ ሰራተኛ)",
            amharicTitle = "የኤሌክትሪክ መስመር ዝርጋታ የቀን ሰራተኞች — ጀሞ",
            category = "ኤሌክትሪክና ቧንቧ (Electrical)",
            location = "አዲስ አበባ፣ ጀሞ 1 (Jemo 1)",
            salaryRange = "800 ETB / ቀን (Daily Rate)",
            employmentType = EmploymentType.DAILY_RATE.name,
            experienceLevel = "መሰረታዊ እውቀት ያለው",
            description = "ለG+3 ሕንፃ የኤሌክትሪክ ፓይፕ ማሳለፍ፣ ዋየር መሳብ እና ቆጣሪ መግጠም ስራ የሚያግዙ 4 የቀን ረዳት ሰራተኞች ለ10 ቀናት እንፈልጋለን። ክፍያ በቀን 800 ብር ማታ ማታ ይከፈላል።",
            deadline = "2026-12-10",
            projectType = "DAILY_RATE",
            budgetAmount = 800.0,
            deliveryDays = 10,
            skillsRequired = "የኤሌክትሪክ መስመር እውቀት፣ ፈጣን ታታሪነት፣ ደህንነት መጠበቅ",
            proposalsCount = 14,
            employerPhone = "+251966778899"
        ),
        JobEntity(
            id = "job_eth_7",
            employerId = "usr_seller_lalibela",
            employerName = "ሰብለወንጌል ተሾመ (የላሊበላ ቅርስ)",
            employerVerified = true,
            title = "Traditional Vestment Embroidery Artisan (የጥልፍና የልብሰ ተክህኖ ሰሪ)",
            amharicTitle = "የካህናት ልብስና የባህል ጥልፍ ባለሙያ — ሾላ",
            category = "ባህልና ጥበብ (Crafts)",
            location = "አዲስ አበባ፣ ሾላ ገበያ (Shola)",
            salaryRange = "25,000 ETB (በስራው ብዛት)",
            employmentType = EmploymentType.CONTRACT.name,
            experienceLevel = "ልምድ ያለው ባለሙያ",
            description = "የካህናትና የዲያቆናት ልብሰ ተክህኖ ጥልፎችን በሐር ክር የሚያዘጋጁ፣ በቤት ተቀምጠውም ሰርተው የሚያስረክቡ ባለሙያዎችን በኮንትራት ስራ እንቀጥራለን።",
            deadline = "2026-12-28",
            projectType = "CONTRACT",
            budgetAmount = 25000.0,
            deliveryDays = 15,
            skillsRequired = "የእጅ ጥልፍ፣ የልብስ ስፌት፣ የቤተክርስቲያን ስርዓት እውቀት",
            proposalsCount = 5,
            employerPhone = "+251977889911"
        ),
        JobEntity(
            id = "job_eth_8",
            employerId = "usr_emp_selam",
            employerName = "ተዋሕዶ ግሎባል ንግድ (የታክስና ሂሳብ ቢሮ)",
            employerVerified = true,
            title = "Part-Time Accountant & Tax Preparer (የትርፍ ሰዓት የሂሳብ ባለሙያ)",
            amharicTitle = "የትርፍ ሰዓት የሂሳብና የግብር ሰራተኛ (ሳምንት 3 ቀን) — ልደታ",
            category = "ፋይናንስና ሂሳብ (Accounting)",
            location = "አዲስ አበባ፣ ልደታ (Lideta)",
            salaryRange = "9,000 ETB / ወር",
            employmentType = EmploymentType.PART_TIME.name,
            experienceLevel = "ዲግሪ ወይም ዲፕሎማ",
            description = "በሳምንት 3 ቀናት (ሰኞ፣ ረቡዕ፣ ዓርብ) እየመጣ የፒችትሪ (Peachtree) መዝገብ የሚያስተካክል፣ የቫት (VAT) መግለጫ የሚያዘጋጅ ታማኝ የሂሳብ ባለሙያ እንፈልጋለን።",
            deadline = "2026-12-22",
            projectType = "PART_TIME",
            budgetAmount = 9000.0,
            deliveryDays = 30,
            skillsRequired = "Peachtree, የታክስ ህግጋት፣ ኤክሴል፣ የሂሳብ አያያዝ",
            proposalsCount = 8,
            employerPhone = "+251988990011"
        )
    )

    val products = listOf(
        ProductEntity(
            id = "prod_1",
            sellerId = "usr_seller_lalibela",
            sellerName = "Lalibela Sacred Vestments",
            sellerVerified = true,
            title = "Hand-Embroidered Clergy Vestment (የካህናት ልብሰ ተክህኖ)",
            amharicTitle = "በእጅ የተጠለፈ የካህናት ልብሰ ተክህኖ ሙሉ ስብስብ",
            description = "የተመረጠ የሐር ክር ጥልፍ ያለበት፣ እጅግ የተዋበ እና ጥራት ካለው ጨርቅ የተሰናዳ የካህናት ልብሰ ተክህኖ። ከነአክሊሉ እና ሞጣሕቱ ጋር።",
            category = "Religious & Cultural",
            price = 14500.0,
            quantity = 15,
            location = "Addis Ababa, Shola",
            deliveryInfo = "በአዲስ አበባ በነፃ እናደርሳለን፤ በክልሎች በፖስታ ወይም በጭነት ይላካል።",
            condition = "አዲስ (Brand New)",
            isFeatured = true
        ),
        ProductEntity(
            id = "prod_2",
            sellerId = "usr_seller_lalibela",
            sellerName = "Lalibela Sacred Vestments",
            sellerVerified = true,
            title = "Handcrafted Brass Orthodox Censer (ጽንሐሕ)",
            amharicTitle = "የነሐስ ጽንሐሕ ከነማዕጠንቱ በልዩ ጌጥ የተሰራ",
            description = "ከንፁህ ናስና ነሐስ በጥንቃቄ የተቀረጸ፣ ጠንካራ ሰንሰለት እና ደወሎች ያሉት ጥንታዊ ቅርፅ የጠበቀ ጽንሐሕ።",
            category = "Religious & Cultural",
            price = 5800.0,
            quantity = 28,
            location = "Addis Ababa",
            deliveryInfo = "ቀጥታ ከእደ-ጥበብ ማዕከል ማድረስ ይቻላል።",
            condition = "አዲስ (Brand New)",
            isFeatured = true
        ),
        ProductEntity(
            id = "prod_3",
            sellerId = "usr_seller_lalibela",
            sellerName = "Lalibela Sacred Vestments",
            sellerVerified = true,
            title = "Pure Virgin Beeswax Candles Bundle (ንጹሕ የሰም ሻማ 100 ፍሬ)",
            amharicTitle = "ንጹሕ የሰም ሻማ ጥቅል (100 ፍሬ የደብር ሻማ)",
            description = "ከማር ሰም የተጠመዘዘ፣ መአዛው ደስ የሚል፣ ጭስ የሌለውና ለረጅም ሰዓት የሚበራ ንጹሕ የኦርቶዶክስ ሻማ።",
            category = "Religious & Cultural",
            price = 3200.0,
            quantity = 150,
            location = "Addis Ababa & Debre Zeit",
            deliveryInfo = "በጅምላ እና በችርቻሮ ይገኛል።",
            condition = "አዲስ (Brand New)",
            isFeatured = false
        ),
        ProductEntity(
            id = "prod_4",
            sellerId = "usr_admin",
            sellerName = "Tewahido Net Cooperative",
            sellerVerified = true,
            title = "Organic White Forest Honey (5kg ደብረ ዘይት አንጓች ማር)",
            amharicTitle = "ኦርጋኒክ ነጭ የጫካ ማር 5 ኪ.ግ ቆርቆሮ",
            description = "ምንም አይነት ቅልቅል የሌለው፣ ለጤና እጅግ ተመራጭ የሆነ ከታመኑ አርሶ አደሮች ማኅበር በቀጥታ የቀረበ የተጣራ ማር።",
            category = "Food & Agriculture",
            price = 4500.0,
            quantity = 60,
            location = "Bole Bulbula",
            deliveryInfo = "ቤት ድረስ እናቀርባለን።",
            condition = "አዲስ (Fresh Harvest)",
            isFeatured = true
        ),
        ProductEntity(
            id = "prod_5",
            sellerId = "usr_emp_selam",
            sellerName = "Selam Technology Solutions",
            sellerVerified = true,
            title = "Smart Solar Backup Inverter 5kVA + LiFePO4 Battery",
            amharicTitle = "ስማርት የሶላርና የመብራት መጠባበቂያ ሲስተም 5kVA",
            description = "ለቢሮዎች፣ ለሆስፒታሎችና ለአብነት ትምህርት ቤቶች አስተማማኝ የመብራት ኃይል ሰጪ። የ5 ዓመት ዋስትና ያለው።",
            category = "Electronics & Power",
            price = 168000.0,
            quantity = 8,
            location = "Kazanchis",
            deliveryInfo = "ነፃ ተከላ እና የቴክኒክ ድጋፍ አለው።",
            condition = "አዲስ (Brand New)",
            isFeatured = true
        )
    )

    val b2bListings = listOf(
        B2BEntity(
            id = "b2b_1",
            userId = "usr_imp_yared",
            companyName = "Tewahido Global Import-Export",
            isVerified = true,
            b2bType = "IMPORTER",
            title = "Food-Grade Kraft Paper Bags & Packaging (ከቱርክ የቀረበ የጅምላ ማሸጊያ)",
            productOrigin = "Turkey (ቱርክ)",
            wholesalePrice = 18.5,
            minimumOrderQty = "5,000 Bags (ቦርሳዎች)",
            totalAvailableQty = "200,000 Units in Stock",
            location = "Kality Industrial Warehouse",
            deliveryTerms = "Ex-Warehouse or Port to Factory Delivery",
            description = "ለዱቄት፣ ለቅመማ ቅመም፣ ለቡናና ለምግብ ፋብሪካዎች የሚሆኑ ጠንካራ ባለ 3 ደርብ ማሸጊያዎች በፋብሪካ ዋጋ ለአከፋፋዮች እናቀርባለን።",
            contactPhone = "+251944556677",
            contactEmail = "yared@tewahidoglobal.com"
        ),
        B2BEntity(
            id = "b2b_2",
            userId = "usr_imp_yared",
            companyName = "Tewahido Global Import-Export",
            isVerified = true,
            b2bType = "IMPORTER",
            title = "Heavy-Duty Construction Rebar & Structural Steel Beams",
            productOrigin = "Ukraine / Turkey",
            wholesalePrice = 115.0,
            minimumOrderQty = "25 Metric Tons",
            totalAvailableQty = "500 Tons Available",
            location = "Gotera Steel Depot",
            deliveryTerms = "FOB Modjo Dry Port / Ex-Stock",
            description = "ደረጃቸውን የጠበቁ የህንፃ ብረቶች (Fe 500D) በሕጋዊ ደረሰኝ ለግንባታ ተቋራጮችና ለአከፋፋዮች።",
            contactPhone = "+251944556677",
            contactEmail = "sales@tewahidoglobal.com"
        ),
        B2BEntity(
            id = "b2b_3",
            userId = "usr_dist_admas",
            companyName = "Admas Distribution Network",
            isVerified = true,
            b2bType = "DISTRIBUTOR",
            title = "Nationwide Wholesale Distribution of Consumer Goods & Edible Oils",
            productOrigin = "Ethiopia / Local & Import",
            wholesalePrice = 850.0,
            minimumOrderQty = "100 Cartons / Jerrycans",
            totalAvailableQty = "Continuous Supply",
            location = "Kality, Adama, Hawassa, Bahir Dar Depots",
            deliveryTerms = "Direct Regional Delivery by Fleet",
            description = "በመላ ሀገሪቱ ላሉ ሱቆችና ንግዶች የምግብ ዘይት፣ ሳሙናዎችና መሰረታዊ ሸቀጦችን በታማኝነት እናከፋፍላለን። ከታመኑ አምራቾች ጋር እንሰራለን።",
            contactPhone = "+251955667788",
            contactEmail = "admas.dist@ethionet.et"
        )
    )

    val services = listOf(
        ServiceEntity(
            id = "svc_1",
            providerId = "usr_svc_dawit",
            providerName = "Eng. Dawit Gebre (Betelhem Studio)",
            isVerified = true,
            title = "Orthodox Church & Complex Architectural Design",
            amharicTitle = "የቤተክርስቲያን ህንፃ፣ አዳራሽና የገዳማት ዲዛይን እና ምህንድስና",
            category = "Architecture & Engineering",
            rateDescription = "እንደ ፕሮጀክቱ ስፋት በስምምነት",
            location = "Addis Ababa & All Regions",
            description = "የጥንታዊ የኢትዮጵያ ኦርቶዶክስ ተዋሕዶ ቤተክርስቲያን የስነ-ህንፃ ቅርስና ቅርፅን ከዘመናዊ የምህንድስና ጥበብ ጋር በማዋሃድ ውብ ህንፃዎችን እናቅዳለን።",
            experienceYears = 12,
            phone = "+251966778899",
            rating = 5.0f,
            reviewsCount = 23
        ),
        ServiceEntity(
            id = "svc_2",
            providerId = "usr_seeker_marta",
            providerName = "Marta Woldemichael (CPA)",
            isVerified = true,
            title = "Corporate Tax Advisory, IFRS Bookkeeping & Audit",
            amharicTitle = "የታክስ፣ የሂሳብ አያያዝ (IFRS) እና የውስጥ ኦዲት አገልግሎት",
            category = "Finance & Legal",
            rateDescription = "በወር ከ 12,000 ETB ጀምሮ",
            location = "Addis Ababa",
            description = "ለንግድ ድርጅቶችና ለማኅበራት የታክስ ክፍያዎች ሕጋዊ አፈጻጸም፣ የፒችትሪ ሲስተም ዝርጋታ እና የፋይናንስ ቁጥጥር አገልግሎት በታማኝነት እሰጣለሁ።",
            experienceYears = 8,
            phone = "+251922334455",
            rating = 4.95f,
            reviewsCount = 18
        ),
        ServiceEntity(
            id = "svc_3",
            providerId = "usr_emp_selam",
            providerName = "Selam Technology Solutions",
            isVerified = true,
            title = "Enterprise Software, Mobile Apps & Web Development",
            amharicTitle = "የድርጅት ሶፍትዌሮች፣ የሞባይል አፕሊኬሽን እና ድረ-ገፅ ዝርጋታ",
            category = "Technology",
            rateDescription = "እንደ ሲስተሙ መጠን በስምምነት",
            location = "Addis Ababa (Kazanchis)",
            description = "ለንግድዎ ዘመናዊ የሽያጭ (POS)፣ የሰራተኞችና የክምችት አስተዳደር እንዲሁም የመስመር ላይ ግብይት ሲስተሞችን እንገነባለን።",
            experienceYears = 7,
            phone = "+251911556677",
            rating = 4.9f,
            reviewsCount = 14
        )
    )

    val applications = listOf(
        ApplicationEntity(
            id = "app_1",
            jobId = "job_2",
            jobTitle = "Senior Financial Auditor & Tax Consultant",
            applicantId = "usr_seeker_marta",
            applicantName = "Marta Woldemichael",
            applicantEmail = "marta.wolde@gmail.com",
            applicantPhone = "+251922334455",
            resumeSummary = "8 ዓመት የባንክና የድርጅት ኦዲት ልምድ። በአዲስ አበባ ዩኒቨርሲቲ በአካውንቲንግ ማስተርስ አለኝ።",
            coverNote = "የክርስቲያናዊ ስነ-ምግባር የታነፀበት፣ ታማኝና ጥራት ያለው የስራ አገልግሎት ለድርጅታችሁ ለማበርከት በታላቅ ክብር አመለክታለሁ።",
            status = ApplicationStatus.INTERVIEW.name
        )
    )

    val conversations = listOf(
        ConversationEntity(
            id = "conv_1",
            participant1Id = "usr_admin",
            participant2Id = "usr_seeker_marta",
            participant1Name = "ዲ/ን አማኑኤል ተስፋዬ",
            participant2Name = "ማርታ ወልደሚካኤል",
            lastMessage = "የአባልነት ማረጋገጫዎ ጸድቋል፤ እንኳን ደስ ያለዎት!",
            lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 30,
            unreadCountUser1 = 0,
            unreadCountUser2 = 0
        ),
        ConversationEntity(
            id = "conv_2",
            participant1Id = "usr_emp_selam",
            participant2Id = "usr_seeker_marta",
            participant1Name = "Eng. Michael (Selam Tech)",
            participant2Name = "ማርታ ወልደሚካኤል",
            lastMessage = "ማክሰኞ 4:00 ሰዓት በቢሮአችን ለቃል መጠይቅ ተጋብዘዋል።",
            lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 15,
            unreadCountUser1 = 0,
            unreadCountUser2 = 1
        )
    )

    val messages = listOf(
        MessageEntity(
            id = "msg_1",
            conversationId = "conv_1",
            senderId = "usr_admin",
            senderName = "ዲ/ን አማኑኤል ተስፋዬ",
            recipientId = "usr_seeker_marta",
            content = "ሰላም ለእርስዎ ይሁን እህታችን ማርታ፤ የላኩት የደብር ማረጋገጫ ሰነድ ታይቶ የአባልነት ማረጋገጫዎ ጸድቋል፤ እንኳን ደስ ያለዎት!",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
            isRead = true
        ),
        MessageEntity(
            id = "msg_2",
            conversationId = "conv_2",
            senderId = "usr_emp_selam",
            senderName = "Eng. Michael (Selam Tech)",
            recipientId = "usr_seeker_marta",
            content = "ሰላም ማርታ፤ ለከፍተኛ ኦዲተርነት ያቀረቡትን ሲቪ በጥልቀት መርምረነዋል። የሥራ ልምድዎ እጅግ የሚያስመሰግን ነው። ማክሰኞ 4:00 ሰዓት በቢሮአችን ለቃል መጠይቅ ተጋብዘዋል።",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
            isRead = false
        )
    )

    val notifications = listOf(
        NotificationEntity(
            id = "notif_1",
            userId = "usr_seeker_marta",
            title = "Job Interview Invitation",
            amharicTitle = "የቃል መጠይቅ ጥሪ ደርሶዎታል!",
            message = "Selam Technology Solutions PLC has shortlisted your application for Senior Financial Auditor.",
            type = "STATUS_CHANGE",
            isRead = false,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 15
        ),
        NotificationEntity(
            id = "notif_2",
            userId = "usr_seeker_marta",
            title = "Verification Approved",
            amharicTitle = "የአባልነት ማረጋገጫዎ ጸድቋል!",
            message = "Your Orthodox Member verification badge has been approved by the platform administrator.",
            type = "VERIFICATION",
            isRead = true,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 35
        ),
        NotificationEntity(
            id = "notif_3",
            userId = "usr_admin",
            title = "New Verification Request",
            amharicTitle = "አዲስ የአባልነት ማረጋገጫ ጥያቄ ደርሷል",
            message = "Yonas Tadesse has submitted identity and parish documentation for review.",
            type = "VERIFICATION",
            isRead = false,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60
        )
    )

    val reviews = listOf(
        ReviewEntity(
            id = "rev_1",
            targetUserId = "usr_seller_lalibela",
            authorId = "usr_admin",
            authorName = "ዲ/ን አማኑኤል ተስፋዬ",
            rating = 5,
            comment = "የካህናት አልባሳቱ እጅግ በጣም ያማሩና ጥራታቸው የተጠበቀ ነው። ታማኝና በሰዓቱ የሚያደርሱ ታታሪ ክርስቲያን ናቸው። እግዚአብሔር ያክብርልን።"
        ),
        ReviewEntity(
            id = "rev_2",
            targetUserId = "usr_svc_dawit",
            authorId = "usr_emp_selam",
            authorName = "Eng. Michael",
            rating = 5,
            comment = "መሐንዲስ ዳዊት የቤተክርስቲያን ህንፃ ዲዛይናቸውን በከፍተኛ ጥንቃቄና ጥበብ ነው ያዘጋጁልን። በስራቸው እጅግ ረክተናል።"
        )
    )

    val reports = listOf(
        ReportEntity(
            id = "rep_1",
            reporterId = "usr_admin",
            reporterName = "System Sentinel",
            targetType = "PRODUCT",
            targetId = "fake_test_item",
            targetTitle = "Suspicious Unregistered Medicine Ad",
            reason = "SPAM",
            details = "Unregulated medical product without certification was flagged automatically for administrator review.",
            status = "PENDING"
        )
    )

    val cvResumes = listOf(
        CVEntity(
            id = "cv_marta_1",
            userId = "usr_seeker_marta",
            fullName = "Marta Wolde-Mikael (CPA)",
            professionTitle = "Senior Financial Auditor & Accounting Lead",
            summary = "Dedicated financial specialist with 8+ years of expertise in bank auditing, tax compliance, financial reporting, and enterprise cost optimization in Addis Ababa.",
            experience = "Senior Auditor at Wegagen Bank (4 yrs), Financial Controller at Abyssinia Holdings (4 yrs)",
            education = "B.A. in Accounting & Finance - Addis Ababa University (Graduated with Great Distinction)",
            skills = "IFRS, Financial Auditing, Peachtree, ERP, Tax Law, Payroll Systems",
            languages = "Amharic (Native), English (Fluent), Ge'ez (Liturgical)",
            certifications = "Certified Public Accountant (CPA Ethiopia), ACCA Level 2",
            fileName = "Marta_Wolde_Auditor_CV_2026.pdf",
            fileType = "PDF",
            fileSizeKb = 345,
            isPrimary = true,
            isPublic = true
        ),
        CVEntity(
            id = "cv_yonas_1",
            userId = "usr_seeker_yonas",
            fullName = "Yonas Tadesse",
            professionTitle = "Senior Mobile Developer (Kotlin & Compose)",
            summary = "Full-stack mobile engineer specialized in scalable Android enterprise architectures, Jetpack Compose, Kotlin Multiplatform, and clean offline-first systems.",
            experience = "Lead Mobile Architect at Fintech PLC (3 yrs), Android Developer at E-Trade Ethiopia (3 yrs)",
            education = "B.Sc. in Computer Science - HilCoE School of Computer Science & Technology",
            skills = "Kotlin, Jetpack Compose, Coroutines, Room DB, Retrofit, KMP, Git, CI/CD",
            languages = "Amharic (Native), English (Fluent), Afaan Oromo (Working)",
            certifications = "Google Certified Associate Android Developer",
            fileName = "Yonas_Tadesse_Android_Resume.pdf",
            fileType = "PDF",
            fileSizeKb = 280,
            isPrimary = true,
            isPublic = true
        )
    )

    val documents = listOf(
        DocumentEntity(
            id = "doc_1",
            userId = "usr_seeker_marta",
            title = "Parish Recommendation Letter",
            docType = "PARISH_RECOMMENDATION",
            fileName = "Bole_MedhaneAlem_Recommendation.pdf",
            fileSizeKb = 420,
            isPublic = false
        ),
        DocumentEntity(
            id = "doc_2",
            userId = "usr_seeker_marta",
            title = "CPA Professional License",
            docType = "CERTIFICATE",
            fileName = "National_CPA_License_2025.pdf",
            fileSizeKb = 512,
            isPublic = true
        ),
        DocumentEntity(
            id = "doc_3",
            userId = "usr_seller_lalibela",
            title = "Official Trade License (TIN/VAT)",
            docType = "BUSINESS_LICENSE",
            fileName = "Ministry_Of_Trade_License_Lalibela.pdf",
            fileSizeKb = 680,
            isPublic = false
        )
    )

    val favorites = listOf(
        FavoriteEntity(
            id = "fav_1",
            userId = "usr_seeker_marta",
            itemType = "JOB",
            itemId = "job_1",
            itemTitle = "Orthodox Church Architectural Designer",
            itemSubtitle = "David Gebre Architectural Studio"
        ),
        FavoriteEntity(
            id = "fav_2",
            userId = "usr_seeker_marta",
            itemType = "PRODUCT",
            itemId = "prod_1",
            itemTitle = "Hand-Embroidered Deacons' Vestments",
            itemSubtitle = "Lalibela Sacred Vestments"
        )
    )

    val verificationRequests = listOf(
        VerificationRequestEntity(
            id = "vreq_1",
            userId = "usr_seeker_yonas",
            userName = "ዮናስ ታደሰ (Yonas Tadesse)",
            userEmail = "yonas.dev@tewahido.et",
            parishChurchName = "መካነ ሰማዕት ቅዱስ ቂርቆስ ቤተክርስቲያን (Mekane Sema'et St. Kirkos)",
            idDocType = "PARISH_CERTIFICATE",
            docFileName = "Kirkos_Parish_Membership_Cert.pdf",
            notes = "Please review my community parish baptism and membership certificate for verified professional badge.",
            status = "PENDING"
        )
    )
}
