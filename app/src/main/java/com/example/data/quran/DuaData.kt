package com.example.data.quran

import com.example.data.model.DailyMoodWisdom
import com.example.data.model.DuaItem

object DuaData {

    val dailyAyah = DuaItem(
        id = "ayah_today",
        category = "Daily Ayah",
        title = "Divine Proximity & Reassurance",
        arabicText = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ",
        transliteration = "Wa-iḏhā sa'alaka ʿibādī ʿannī fa'innī qarīb, ujību daʿwatad-dāʿi iḏhā daʿān",
        translation = "And when My servants ask you concerning Me - indeed I am near. I respond to the invocation of the supplicant when he calls upon Me.",
        reference = "Surah Al-Baqarah (2:186)",
        occasion = "Daily Spiritual Reflection",
        categoryAr = "آية اليوم",
        referenceAr = "سورة البقرة: ١٨٦",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002186.mp3"
    )

    val dailyDua = DuaItem(
        id = "dua_today",
        category = "Daily Dua",
        title = "Supplication for Light & Rectification",
        arabicText = "اللَّهُمَّ اجْعَلْ فِي قَلْبِي نُورًا، وَفِي بَصَرِي نُورًا، وَفِي سَمْعِي نُورًا، وَعَنْ يَمِينِي نُورًا، وَعَنْ يَسَارِي نُورًا",
        transliteration = "Allāhumma-jʿal fī qalbī nūrā, wa fī baṣarī nūrā, wa fī samʿī nūrā, wa ʿan yamīnī nūrā, wa ʿan yasārī nūrā",
        translation = "O Allah, place light in my heart, light in my sight, light in my hearing, light on my right, and light on my left.",
        reference = "Sahih Muslim 763",
        occasion = "Morning & Before Prayer",
        categoryAr = "دعاء اليوم",
        referenceAr = "صحيح مسلم ٧٦٣",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/19.mp3"
    )

    val moodWisdomMap: Map<String, Pair<DailyMoodWisdom, DailyMoodWisdom>> = mapOf(
        "Anxious" to Pair(
            DailyMoodWisdom(
                mood = "Anxious",
                isIslamic = true,
                arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
                translation = "O Allah, I seek refuge in You from anxiety and grief, incapacity and laziness, cowardice and avarice, the burden of debt and being overpowered by men.",
                source = "Sahih Al-Bukhari 6369",
                explanation = "Anxiety often stems from feeling that we must carry the weight of the unknown alone. The Prophet (PBUH) taught this powerful supplication to release heavy emotional burdens and anchor the heart in divine trust.",
                sourceAr = "صحيح البخاري: ٦٣٦٩",
                explanationAr = "علمنا النبي ﷺ هذا الدعاء المبارك لتفريغ الهموم الثقيلة وإلقاء الحمل كله على وكيل الله سبحانه وتعالى."
            ),
            DailyMoodWisdom(
                mood = "Anxious",
                isIslamic = false,
                arabicText = "كُن كَالْمَاءِ، لَا يَشُقُّ عَلَيْهِ أَيُّ مَسَارٍ • الِاسْتِقْرَارُ مَنْبَعُهُ الدَّاخِلُ",
                translation = "Present moment mindfulness: Breath is your anchor; peace is not the absence of storm, but stillness within. Recognize that your current worry is a transient cognitive wave rather than a permanent reality.",
                source = "Mindful Contemplation",
                explanation = "Anchor your awareness to the slow rhythm of conscious breath. When thoughts begin to spiral into scenarios of tomorrow, gently return your attention to the grounded reality of right now.",
                sourceAr = "تأمل وهدوء النفس",
                explanationAr = "اعلم أن القلق عابر كالغيوم، واستحضر هدوء اللحظة الحاضرة والسكينة الداخلية التي تنبع من التقبل والرضا."
            )
        ),
        "Grateful" to Pair(
            DailyMoodWisdom(
                mood = "Grateful",
                isIslamic = true,
                arabicText = "اللَّهُمَّ أَعِنِّى عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
                translation = "O Allah, assist me in remembering You, in thanking You, and in worshipping You in the best manner.",
                source = "Sunan Abi Dawud 1522",
                explanation = "Gratitude (Shukr) is an active spiritual multiplier. When the heart acknowledges divine blessings through remembrance, inner peace and abundance expand.",
                sourceAr = "سنن أبي داود: ١٥٢٢",
                explanationAr = "الشكر ليس مجرد كلمة باللسان، بل استعانة بالله لدوام ذكره وحسن عبادته في كل حين."
            ),
            DailyMoodWisdom(
                mood = "Grateful",
                isIslamic = false,
                arabicText = "الامْتِنَانُ رَبِيعُ الرُّوحِ الدَّائِمُ • يُحَوِّلُ الْقَلِيلَ إِلَى كِفَايَةٍ",
                translation = "Gratitude turns what we have into enough, illuminating the hidden abundance of ordinary moments. Cultivating gratitude rewires neural pathways, fostering emotional resilience and profound contentment with every breath.",
                source = "Universal Wisdom",
                explanation = "Pause to notice the subtle gifts around you—the warmth of light, the comfort of shelter, the miracle of breath. Gratitude is the quiet art of recognizing that wealth resides in a peaceful mind.",
                sourceAr = "حكمة الامتنان",
                explanationAr = "الامتنان يحول القليل إلى كفاية، وينير تفاصيل الحياة بالرضا والبهجة ويدرّب الروح على الاستبشار."
            )
        ),
        "Tired" to Pair(
            DailyMoodWisdom(
                mood = "Tired",
                isIslamic = true,
                arabicText = "يَا حَيُّ يَا قَيُّومُ، بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لي شَأْنِي كُلَّهُ، وَلاَ تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
                translation = "O Ever-Living, O Self-Sustaining, by Your mercy I seek relief; rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
                source = "Sunan An-Nasa'i & Al-Albani Sahih",
                explanation = "When physical and emotional exhaustion weigh heavily, turning to Al-Hayy Al-Qayyum restores spiritual vigor and renews your strength from the source of all life.",
                sourceAr = "سنن النسائي وصحيح الألباني",
                explanationAr = "حين تشعر بالإرهاق، التجئ إلى الحي القيوم الذي بيده ملكوت كل شيء ليستقيم أمرك وتتجدد طاقتك."
            ),
            DailyMoodWisdom(
                mood = "Tired",
                isIslamic = false,
                arabicText = "الرَّاحَةُ لَيْسَتْ اسْتِسْلَامًا، بَلْ هِيَ اسْتِعَادَةٌ لِلْقُوَّةِ",
                translation = "Rest is not a luxury or a sign of weakness; it is a vital act of self-preservation and restoration. Honor your body's signals and allow yourself space to heal without guilt.",
                source = "Mindful Rest",
                explanation = "When the body and spirit feel drained, pushing harder yields diminished returns. Step back, breathe deeply, and give yourself permission to simply exist and recharge.",
                sourceAr = "سكينة واسترجاع طاقة",
                explanationAr = "الاستراحة الحقيقية ليست كسلًا، بل هي شحن لطاقتك العاطفية والروحية لتستأنف مسيرك بتوازن وصحة."
            )
        ),
        "Overwhelmed" to Pair(
            DailyMoodWisdom(
                mood = "Overwhelmed",
                isIslamic = true,
                arabicText = "اللَّهُمَّ لَا سَهْلَ إِلَّا ما جَعَلْتَهُ سَهْلاً، وَأَنْتَ تَجْعَلُ الحَزْنَ إِذَا شِئْتَ سَهْلاً",
                translation = "O Allah, there is no ease except what You make easy, and You, if You wish, can make the difficult easy.",
                source = "Sahih Ibn Hibban 974",
                explanation = "When tasks and thoughts pile up and overwhelm the heart, this concise prophetic dua aligns your spirit with the ultimate facilitator of all affairs.",
                sourceAr = "صحيح ابن حبان: ٩٧٤",
                explanationAr = "دعاء يزيل ثقل الضغوط ويُفوض الأمر لمن بيده تيسير كل عسير."
            ),
            DailyMoodWisdom(
                mood = "Overwhelmed",
                isIslamic = false,
                arabicText = "خُطْوَةٌ وَاحِدَةٌ تَكْفِي لِبَدْءِ الْعَوْدَةِ • تَفْكِيكُ العَقَبَاتِ يَبْدَأُ بِاللَّحْظَةِ الحَاضِرَةِ",
                translation = "You don't have to carry the whole mountain today. Simply focus on this single breath, this single step. Deconstruct daunting obstacles into single present-moment actions to restore cognitive equilibrium.",
                source = "Mindful Living",
                explanation = "Overwhelm happens when the mind attempts to solve fifty future moments at once. Pull your attention back to what is directly in front of you right now, and address it with calm focus.",
                sourceAr = "تيسير وتخفيف",
                explanationAr = "لا تحمل هم الأيام دفعة واحدة، ركز في خطوة اليوم وسيرعاك الله في كل خطوة ومرحلة."
            )
        ),
        "Hopeful" to Pair(
            DailyMoodWisdom(
                mood = "Hopeful",
                isIslamic = true,
                arabicText = "أَنَا عِنْدَ ظَنِّ عَبْدِي بي، وَأَنَا مَعَهُ إِذَا ذَكَرَنِي",
                translation = "Allah says: 'I am as My servant thinks of Me, and I am with him when he remembers Me.'",
                source = "Sahih Al-Bukhari 7405",
                explanation = "Hope in Allah (Husn al-Dhann) is the bedrock of faith. Anticipate goodness, for divine mercy is infinitely vast and meets your positive expectation.",
                sourceAr = "صحيح البخاري: ٧٤٠٥",
                explanationAr = "حسن الظن بالله مفتاح الفرج والقبول، وكلما أملت الخير في ربك وجدته أقرب مما تتصور."
            ),
            DailyMoodWisdom(
                mood = "Hopeful",
                isIslamic = false,
                arabicText = "الأَمَلُ نُورٌ يَنْبُثُقُ مِنْ أَعْمَاقِ الظُّلْمَةِ",
                translation = "Hope is the quiet conviction that light will always overcome shadow. Hold fast to your optimism, for the seeds of today's hopeful patience bloom into tomorrow's strength.",
                source = "Universal Hope",
                explanation = "A hopeful spirit transforms energy into positive action. Trust the timing of your journey and continue moving forward with open arms.",
                sourceAr = "تأمل ورجاء",
                explanationAr = "الأمل ليس أمنية مجردة، بل هو طاقة إيجابية تدفعك للعمل وتستشرف الغد بيقين وتفاؤل."
            )
        ),
        "Lost" to Pair(
            DailyMoodWisdom(
                mood = "Lost",
                isIslamic = true,
                arabicText = "اللَّهُمَّ اهْدِنِي وَسَدِّدْنِي",
                translation = "O Allah, guide me and direct me to the right path.",
                source = "Sahih Muslim 2725",
                explanation = "When direction is unclear and disorientation sets in, asking Al-Hadi for direct guidance and steadfastness brings immediate inner clarity.",
                sourceAr = "صحيح مسلم: ٢٧٢٥",
                explanationAr = "دعاء جامع يطلب الهداية والتسديد في الأقوال والأفعال والخطوات."
            ),
            DailyMoodWisdom(
                mood = "Lost",
                isIslamic = false,
                arabicText = "الصَّمْتُ يَفْتَحُ أَبْوَابَ الْبَصِيرَةِ • الْوُضُوحُ يَأْتِي بَعْدَ السُّكُونِ",
                translation = "Clarity does not arrive from turbulent striving, but from quiet listening to your deepest moral compass. Allow your mind to settle like clear water; the path forward becomes evident when inner noise subsides.",
                source = "Philosophical Insight",
                explanation = "When you feel lost, stop running. Sit quietly, align with your inner core, and trust that the next right step will reveal itself in due time.",
                sourceAr = "بصيرة وهداية",
                explanationAr = "الوضوح ينبع من سكون القلب والتأمل الصادق حين تهدأ ضوضاء التردد والصخب."
            )
        ),
        "Peaceful" to Pair(
            DailyMoodWisdom(
                mood = "Peaceful",
                isIslamic = true,
                arabicText = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الجَلَالِ وَالإِكْرَامِ",
                translation = "O Allah, You are Peace, and from You comes peace. Blessed are You, O Owner of Majesty and Honor.",
                source = "Sahih Muslim 592",
                explanation = "True peace (As-Sakinah) is a divine gift from As-Salam. Savor this state of serenity by expressing gratitude and radiating calm to those around you.",
                sourceAr = "صحيح مسلم: ٥٩٢",
                explanationAr = "التحميد والتسبيح بأسماء السلام يثبت طمأنينة القلب ويديم السكينة."
            ),
            DailyMoodWisdom(
                mood = "Peaceful",
                isIslamic = false,
                arabicText = "السَّكِينَةُ هِيَ التَّنَاغُمُ مَعَ الْحَيَاةِ • الرِّضَا مِفْتَاحُ الهُدُوءِ",
                translation = "Peace is an inside state that reflects harmony with reality as it unfolds. Cherish serenity by savoring stillness and radiating kindness to those around you.",
                source = "Inner Harmony",
                explanation = "When peace fills your heart, protect it like a precious sanctuary. Share this serene warmth with everyone you encounter today.",
                sourceAr = "سكينة وسلام",
                explanationAr = "السلام الداخلي ينبع من الرضا والتصالح مع أقدار الحياة والتناغم مع اللحظة الحاضرة."
            )
        )
    )

    val categories: List<com.example.data.model.DuaCategory> = listOf(
        com.example.data.model.DuaCategory(
            id = "Morning Azkar",
            titleEnglish = "Morning Azkar",
            titleArabic = "أذكار الصباح",
            description = "Divine light & spiritual fortress to start the day",
            itemCount = 6,
            iconType = "morning"
        ),
        com.example.data.model.DuaCategory(
            id = "Evening Azkar",
            titleEnglish = "Evening Azkar",
            titleArabic = "أذكار المساء",
            description = "Tranquility, sanctuary & night protection",
            itemCount = 6,
            iconType = "evening"
        ),
        com.example.data.model.DuaCategory(
            id = "After Salah",
            titleEnglish = "After Salah",
            titleArabic = "أذكار بعد الصلاة",
            description = "Prescribed remembrances following the 5 daily prayers",
            itemCount = 6,
            iconType = "salah"
        ),
        com.example.data.model.DuaCategory(
            id = "Sleep & Awakening",
            titleEnglish = "Sleep & Awakening",
            titleArabic = "أذكار النوم والاستيقاظ",
            description = "Prophetic supplications for rest and waking up",
            itemCount = 5,
            iconType = "sleep"
        ),
        com.example.data.model.DuaCategory(
            id = "Protection & Anxiety",
            titleEnglish = "Protection & Anxiety",
            titleArabic = "الحفظ والسكينة",
            description = "Relief from distress, sorrow, fear & evil eye",
            itemCount = 5,
            iconType = "shield"
        ),
        com.example.data.model.DuaCategory(
            id = "Health & Healing",
            titleEnglish = "Health & Healing",
            titleArabic = "الشفاء والعافية",
            description = "Prophetic Ruqyah and healing for pain and sickness",
            itemCount = 4,
            iconType = "health"
        ),
        com.example.data.model.DuaCategory(
            id = "Travel & Decisions",
            titleEnglish = "Travel & Decisions",
            titleArabic = "السفر والاستخارة",
            description = "Guidance in major life decisions & journey safety",
            itemCount = 4,
            iconType = "travel"
        ),
        com.example.data.model.DuaCategory(
            id = "Quranic Prophetic Duas",
            titleEnglish = "Quranic Duas",
            titleArabic = "أدعية القرآن الكريم",
            description = "Direct prayers of the Prophets (Rabbana) from the Quran",
            itemCount = 6,
            iconType = "quran"
        ),
        com.example.data.model.DuaCategory(
            id = "Praise & Gratitude",
            titleEnglish = "Praise & Gratitude",
            titleArabic = "الحمد والثناء",
            description = "Glorification, thanks and praising Allah for His countless blessings",
            itemCount = 4,
            iconType = "praise"
        ),
        com.example.data.model.DuaCategory(
            id = "Forgiveness & Repentance",
            titleEnglish = "Forgiveness & Repentance",
            titleArabic = "الاستغفار والتوبة",
            description = "Seeking pardon, cleansing the soul, and returning to the path of mercy",
            itemCount = 4,
            iconType = "forgiveness"
        )
    )

    val categorizedDuas: List<DuaItem> = listOf(
        // --- MORNING AZKAR ---
        DuaItem(
            id = "m1",
            category = "Morning Azkar",
            title = "Master of Forgiveness (Sayyid al-Istighfar)",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            transliteration = "Allāhumma anta Rabbī lā ilāha illā anta, khalaqtanī wa anā ʿabduka, wa anā ʿalā ʿahdika wa waʿdika mastataʿtu, aʿūḏhu bika min sharri mā ṣanaʿtu, abū'u laka biniʿmatika ʿalayya, wa abū'u laka biḏhambī faghfir lī fa'innahū lā yaghfiruḏh-ḏhunūba illā ant",
            translation = "O Allah, You are my Lord; there is no deity except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me, and I acknowledge my sin, so forgive me, for indeed none forgives sins except You.",
            reference = "Sahih al-Bukhari 6306",
            occasion = "Recited every morning",
            repeatCount = 1,
            benefit = "Whoever recites it in the morning with firm faith and dies before evening will enter Paradise.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/79.mp3"
        ),
        DuaItem(
            id = "m2",
            category = "Morning Azkar",
            title = "Protection Against All Calamity",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillāhillaḏhī lā yaḍurru maʿasmihī shay'un fil-arḍi walā fis-samā'i wa huwas-Samīʿul-ʿAlīm",
            translation = "In the Name of Allah with Whose Name nothing can harm on earth or in the heavens, and He is the All-Hearing, the All-Knowing.",
            reference = "Sunan Abi Dawud 5088",
            occasion = "Morning (3 Times)",
            repeatCount = 3,
            benefit = "Nothing shall harm the believer throughout the entire day.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/86.mp3"
        ),
        DuaItem(
            id = "m3",
            category = "Morning Azkar",
            title = "Morning Declaration of Dominion & Gratitude",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Aṣbaḥnā wa aṣbaḥal-mulku lillāh, wal-ḥamdu lillāh, lā ilāha illallāhu waḥdahū lā sharīka lah, lahul-mulku wa lahul-ḥamdu wa huwa ʿalā kulli shay'in qadīr",
            translation = "We have entered the morning and the entire dominion belongs to Allah, and all praise is for Allah. There is no deity except Allah alone without partner. To Him belongs sovereignty and praise, and He has power over all things.",
            reference = "Sahih Muslim 2723",
            occasion = "Morning upon awakening",
            repeatCount = 1,
            benefit = "Renews the believer's covenant of Tawhid and submission to Allah.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/77.mp3"
        ),
        DuaItem(
            id = "m4",
            category = "Morning Azkar",
            title = "Seeking Well-Being in Religion, Body & Life",
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي وَآمِنْ رَوْعَاتِي",
            transliteration = "Allāhumma innī as'alukal-ʿāfiyata fid-dunyā wal-ākhirah, Allāhumma innī as'alukal-ʿafwa wal-ʿāfiyata fī dīnī wa dunyāya wa ahlī wa mālī, Allāhummastur ʿawrātī wa āmin rawʿātī",
            translation = "O Allah, I ask You for well-being in this life and the Hereafter. O Allah, I ask You for pardon and well-being in my religion, worldly affairs, family, and wealth. O Allah, conceal my faults and reassure my fears.",
            reference = "Sunan Abi Dawud 5074",
            occasion = "Every morning",
            repeatCount = 1,
            benefit = "Complete prophetic shield covering health, faith, wealth, and family.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/84.mp3"
        ),
        DuaItem(
            id = "m5",
            category = "Morning Azkar",
            title = "Satisfaction with Allah, Islam, and the Prophet",
            arabicText = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
            transliteration = "Raḍītu billāhi Rabbā, wa bil-Islāmi dīnā, wa bi-Muḥammadin ṣallallāhu ʿalayhi wa sallama Nabiyyā",
            translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad (peace and blessings be upon him) as my Prophet.",
            reference = "Sunan Abi Dawud 5072",
            occasion = "Morning (3 Times)",
            repeatCount = 3,
            benefit = "Allah has promised to please the servant who says this on the Day of Judgment.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/87.mp3"
        ),
        DuaItem(
            id = "m6",
            category = "Morning Azkar",
            title = "Tasbih & Praise Equivalent to All Creation",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ",
            transliteration = "Subḥānallāhi wa biḥamdih: ʿadada khalqih, wa riḍā nafsih, wa zinata ʿarshih, wa midāda kalimātih",
            translation = "Glory is to Allah and praise is to Him: by the number of His creation, by His pleasure, by the weight of His Throne, and by the ink of His words.",
            reference = "Sahih Muslim 2726",
            occasion = "Morning (3 Times)",
            repeatCount = 3,
            benefit = "Weighs more heavily on the scales than hours of ordinary continuous remembrance.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/94.mp3"
        ),

        // --- EVENING AZKAR ---
        DuaItem(
            id = "e1",
            category = "Evening Azkar",
            title = "Evening Declaration of Kingdom & Praise",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Amsaynā wa amsal-mulku lillāh, wal-ḥamdu lillāh, lā ilāha illallāhu waḥdahū lā sharīka lah, lahul-mulku wa lahul-ḥamdu wa huwa ʿalā kulli shay'in qadīr",
            translation = "We have entered the evening and sovereignty belongs to Allah, and all praise is for Allah. There is no deity except Allah alone without partner. To Him belongs the dominion and praise, and He is over all things capable.",
            reference = "Sahih Muslim 2723",
            occasion = "Recited at sunset / evening",
            repeatCount = 1,
            benefit = "Brings serenity and protection against the trials of the night.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/77.mp3"
        ),
        DuaItem(
            id = "e2",
            category = "Evening Azkar",
            title = "Seeking Refuge in the Perfect Words of Allah",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            transliteration = "Aʿūḏhu bi-kalimātillāhit-tāmmāti min sharri mā khalaq",
            translation = "I seek refuge in the perfect words of Allah from the evil of that which He has created.",
            reference = "Sahih Muslim 2709",
            occasion = "Evening (3 Times)",
            repeatCount = 3,
            benefit = "No venomous bite, harm, or fever will touch the supplicant that night.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/97.mp3"
        ),
        DuaItem(
            id = "e3",
            category = "Evening Azkar",
            title = "The 3 Protective Surahs (Al-Mu'awwidhat)",
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
            transliteration = "Surah Al-Ikhlas, Surah Al-Falaq, Surah An-Nas",
            translation = "Recite Surah Al-Ikhlas, Al-Falaq, and An-Nas three times in the morning and evening.",
            reference = "Sunan Abi Dawud 5082",
            occasion = "Evening (3 Times each)",
            repeatCount = 3,
            benefit = "They will suffice you against all evils and harm in creation.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/76.mp3"
        ),
        DuaItem(
            id = "e4",
            category = "Evening Azkar",
            title = "Evening Affirmation of Divine Protection",
            arabicText = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
            transliteration = "Allāhumma bika amsaynā, wa bika aṣbaḥnā, wa bika naḥyā, wa bika namūtu, wa ilaykal-maṣīr",
            translation = "O Allah, by Your grace we have reached the evening, by Your grace we reached the morning, by You we live, by You we die, and to You is our final return.",
            reference = "Sunan at-Tirmidhi 3391",
            occasion = "Evening",
            repeatCount = 1,
            benefit = "Deepens mindful awareness of human mortality and reliance on Allah.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/78.mp3"
        ),
        DuaItem(
            id = "e5",
            category = "Evening Azkar",
            title = "Supplication for Sufficiency (Hasbiyallahu)",
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            transliteration = "Ḥasbiyallāhu lā ilāha illā huwa ʿalayhi tawakkaltu wa huwa Rabbul-ʿArshil-ʿAẓīm",
            translation = "Allah is sufficient for me; there is no deity except Him. Upon Him I have relied, and He is the Lord of the Great Throne.",
            reference = "Sunan Abi Dawud 5081",
            occasion = "Evening (7 Times)",
            repeatCount = 7,
            benefit = "Allah will suffice the reciter against whatever worries them in this world and the Next.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/83.mp3"
        ),
        DuaItem(
            id = "e6",
            category = "Evening Azkar",
            title = "Seeking Divine Light & Protection From 4 Directions",
            arabicText = "اللَّهُمَّ احْفَظْنِي مِنْ بَيْنِ يَدَيَّ، وَمِنْ خَلْفِي، وَعَنْ يَمِينِي، وَعَنْ شِمَالِي، وَمِنْ فَوْقِي، وَأَعُوذُ بِعَظَمَتِكَ أَنْ أُغْتَالَ مِنْ تَحْتِي",
            transliteration = "Allāhummaḥfaẓnī mim bayni yadayya, wa min khalfī, wa ʿan yamīnī, wa ʿan shimālī, wa min fawqī, wa aʿūḏhu bi-ʿaẓamatika an ughtāla min taḥtī",
            translation = "O Allah, protect me from in front of me and behind me, from my right and my left, and from above me; and I seek refuge in Your greatness from being struck from beneath me.",
            reference = "Sunan Abi Dawud 5074",
            occasion = "Evening",
            repeatCount = 1,
            benefit = "A comprehensive 6-directional spiritual fortress of protection.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/84.mp3"
        ),

        // --- AFTER SALAH ---
        DuaItem(
            id = "s1",
            category = "After Salah",
            title = "Seeking Forgiveness & Supplication for Peace",
            arabicText = "أَسْتَغْفِرُ اللَّهَ (٣x)، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            transliteration = "Astaghfirullāh (3x), Allāhumma antas-Salāmu wa minkas-salām, tabārakta yā Ḏhal-Jalāli wal-Ikrām",
            translation = "I seek the forgiveness of Allah (3 times). O Allah, You are Peace and from You comes peace. Blessed are You, O Possessor of majesty and honor.",
            reference = "Sahih Muslim 591",
            occasion = "Immediately following the Salam of every obligatory prayer",
            repeatCount = 3,
            benefit = "Purifies any shortcomings during prayer and invokes divine serenity.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/66.mp3"
        ),
        DuaItem(
            id = "s2",
            category = "After Salah",
            title = "The Throne Verse (Ayat al-Kursi)",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ",
            transliteration = "Allāhu lā ilāha illā huwal-ḥayyul-qayyūm...",
            translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence...",
            reference = "Sunan an-Nasa'i 9848",
            occasion = "After every obligatory prayer",
            repeatCount = 1,
            benefit = "Whoever recites it after every prescribed prayer, nothing stands between him and entering Paradise except death.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"
        ),
        DuaItem(
            id = "s3",
            category = "After Salah",
            title = "SubhanAllah (Glory be to Allah)",
            arabicText = "سُبْحَانَ اللَّهِ",
            transliteration = "Subḥānallāh",
            translation = "Glory be to Allah in His infinite perfection.",
            reference = "Sahih Muslim 597",
            occasion = "Post-Salah Tasbih (33 Times)",
            repeatCount = 33,
            benefit = "Part of the sacred 99-bead post-prayer remembrance.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/68.mp3"
        ),
        DuaItem(
            id = "s4",
            category = "After Salah",
            title = "Alhamdulillah (All Praise to Allah)",
            arabicText = "الْحَمْدُ لِلَّهِ",
            transliteration = "Al-ḥamdu lillāh",
            translation = "All praise and gratitude are due exclusively to Allah.",
            reference = "Sahih Muslim 597",
            occasion = "Post-Salah Tahmid (33 Times)",
            repeatCount = 33,
            benefit = "Fills the heavenly scales with radiant spiritual reward.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/68.mp3"
        ),
        DuaItem(
            id = "s5",
            category = "After Salah",
            title = "Allahu Akbar (Allah is the Greatest)",
            arabicText = "اللَّهُ أَكْبَرُ",
            transliteration = "Allāhu Akbar",
            translation = "Allah is Greater than all creation, worry, and difficulty.",
            reference = "Sahih Muslim 597",
            occasion = "Post-Salah Takbir (33 Times)",
            repeatCount = 33,
            benefit = "Elevates the soul and magnifies divine grandeur.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/68.mp3"
        ),
        DuaItem(
            id = "s6",
            category = "After Salah",
            title = "Seal of the 100 Post-Prayer Remembrances",
            arabicText = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Lā ilāha illallāhu waḥdahū lā sharīka lah, lahul-mulku wa lahul-ḥamdu, wa huwa ʿalā kulli shay'in qadīr",
            translation = "There is no deity except Allah alone without partner. To Him belongs all sovereignty and praise, and He has power over everything.",
            reference = "Sahih Muslim 597",
            occasion = "100th Tasbih Completion",
            repeatCount = 1,
            benefit = "Sins will be forgiven even if they are like the foam of the sea.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/68.mp3"
        ),

        // --- SLEEP & AWAKENING ---
        DuaItem(
            id = "sl1",
            category = "Sleep & Awakening",
            title = "Supplication Before Sleeping",
            arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            transliteration = "Bismika Rabbī waḍaʿtu jambī wa bika arfaʿuh, fa'in amsakta nafsī farḥamhā, wa in arsaltahā faḥfaẓhā bimā taḥfaẓu bihī ʿibādakaṣ-ṣāliḥīn",
            translation = "In Your Name my Lord, I lay down my side and by You I raise it up. If You take my soul, have mercy upon it, and if You send it back, protect it as You protect Your righteous servants.",
            reference = "Sahih al-Bukhari 6320",
            occasion = "Before sleeping on the right side",
            repeatCount = 1,
            benefit = "Entrusts the sleeping soul directly into Allah's gentle custody.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/102.mp3"
        ),
        DuaItem(
            id = "sl2",
            category = "Sleep & Awakening",
            title = "Short Dua for Sleep with Divine Name",
            arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            transliteration = "Bismikallāhumma amūtu wa aḥyā",
            translation = "In Your Name, O Allah, I die and I live.",
            reference = "Sahih al-Bukhari 6312",
            occasion = "Immediately before closing eyes",
            repeatCount = 1,
            benefit = "Attunes the heart to the divine name as consciousness fades.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/105.mp3"
        ),
        DuaItem(
            id = "sl3",
            category = "Sleep & Awakening",
            title = "Dua Upon Awakening from Sleep",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Al-ḥamdu lillāhillaḏhī aḥyānā baʿda mā amātanā wa ilayhin-nushūr",
            translation = "All praise is for Allah who gave us life after having given us death, and unto Him is the resurrection.",
            reference = "Sahih al-Bukhari 6312",
            occasion = "Upon opening eyes in the morning",
            repeatCount = 1,
            benefit = "Starts the morning with instant gratitude for the gift of life.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/1.mp3"
        ),
        DuaItem(
            id = "sl4",
            category = "Sleep & Awakening",
            title = "Tasbih of Fatimah (RA) Before Bed",
            arabicText = "سُبْحَانَ اللَّهِ (٣٣x)، الْحَمْدُ لِلَّهِ (٣٣x)، اللَّهُ أَكْبَرُ (٣٤x)",
            transliteration = "SubhanAllah (33x), Alhamdulillah (33x), Allahu Akbar (34x)",
            translation = "Glorifying, praising, and magnifying Allah before sleep totaling 100.",
            reference = "Sahih al-Bukhari 5361",
            occasion = "Bedtime ritual",
            repeatCount = 3,
            benefit = "Gives spiritual vigor and strength greater than any servant or worldly helper.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/106.mp3"
        ),
        DuaItem(
            id = "sl5",
            category = "Sleep & Awakening",
            title = "Last 2 Verses of Surah Al-Baqarah (285-286)",
            arabicText = "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ...",
            transliteration = "Āmanar-Rasūlu bimā unzila ilayhi mir-Rabbihī wal-mu'minūn...",
            translation = "The Messenger has believed in what was revealed to him from his Lord, and so have the believers...",
            reference = "Sahih al-Bukhari 5009",
            occasion = "Night recitation before sleep",
            repeatCount = 1,
            benefit = "Whoever recites them at night, they will suffice him against every evil.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002285.mp3"
        ),

        // --- PROTECTION & ANXIETY ---
        DuaItem(
            id = "p1",
            category = "Protection & Anxiety",
            title = "Dua for Alleviating Anxiety, Sorrow & Heavy Debt",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allāhumma innī aʿūḏhu bika minal-hammi wal-ḥazan, wal-ʿajzi wal-kasal, wal-bukhli wal-jubn, wa ḍalaʿid-dayni wa ghalabatir-rijāl",
            translation = "O Allah, I seek refuge in You from anxiety and sorrow, from weakness and laziness, from miserliness and cowardice, and from the burden of debt and being overpowered by men.",
            reference = "Sahih al-Bukhari 2893",
            occasion = "Times of psychological distress or financial strain",
            repeatCount = 1,
            benefit = "Dissolves emotional paralysis and breaks the spiritual weight of debt.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/121.mp3"
        ),
        DuaItem(
            id = "p2",
            category = "Protection & Anxiety",
            title = "Dua of Prophet Yunus (AS) in Deep Anguish",
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            transliteration = "Lā ilāha illā anta subḥānaka innī kuntu minaẓ-ẓālimīn",
            translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            reference = "Surah Al-Anbiya (21:87)",
            occasion = "Direct relief from deep darkness and crises",
            repeatCount = 1,
            benefit = "No Muslim supplicates with this in any distress except that Allah relieves him.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/021087.mp3"
        ),
        DuaItem(
            id = "p3",
            category = "Protection & Anxiety",
            title = "Supplication of Absolute Reliance (Tawakkul)",
            arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "Ḥasbunallāhu wa niʿmal-wakīl",
            translation = "Allah is sufficient for us, and He is the best Disposer of affairs.",
            reference = "Surah Ali 'Imran (3:173)",
            occasion = "Times of danger or uncertainty",
            repeatCount = 7,
            benefit = "Spoken by Ibrahim (AS) in fire and Muhammad (PBUH) in battle.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/003173.mp3"
        ),
        DuaItem(
            id = "p4",
            category = "Protection & Anxiety",
            title = "Relief from Difficult Affairs & Hardships",
            arabicText = "اللَّهُمَّ لَا سَهْلَ إِلَّا مَا جَعَلْتَهُ سَهْلًا، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلًا",
            transliteration = "Allāhumma lā sahla illā mā jaʿaltahū sahlā, wa anta tajʿalul-ḥazna iḏhā shi'ta sahlā",
            translation = "O Allah, there is no ease except that which You make easy, and You make hardship easy if You will.",
            reference = "Sahih Ibn Hibban 974",
            occasion = "Before exams, difficult tasks, or challenging interviews",
            repeatCount = 1,
            benefit = "Opens closed pathways and softens challenging circumstances.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/139.mp3"
        ),
        DuaItem(
            id = "p5",
            category = "Protection & Anxiety",
            title = "Seeking Protection from the Evil Eye and Devils",
            arabicText = "أُعِيذُكُمْ بِكَلِمَاتِ اللَّهِ التَّامَّةِ مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ، وَمِنْ كُلِّ عَيْنٍ لَامَّةٍ",
            transliteration = "Uʿīḏhukum bi-kalimātillāhit-tāmmati min kulli shayṭānin wa hāmmah, wa min kulli ʿaynin lāmmah",
            translation = "I seek refuge for you in the perfect words of Allah from every devil and poisonous creature, and from every envious evil eye.",
            reference = "Sahih al-Bukhari 3371",
            occasion = "Protection for children and loved ones",
            repeatCount = 3,
            benefit = "The exact prayer the Prophet (PBUH) recited over Hasan and Husain.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/146.mp3"
        ),

        // --- HEALTH & HEALING ---
        DuaItem(
            id = "h1",
            category = "Health & Healing",
            title = "Prophetic Healing for Illness and Bodily Pain",
            arabicText = "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَاسَ، اشْفِهِ وَأَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            transliteration = "Allāhumma Rabban-nās aḏhhibil-ba's, ishfi wa anta-Shāfī, lā shifā'a illā shifā'uk, shifā'an lā yughādiru saqamā",
            translation = "O Allah, Lord of mankind, remove the hardship, heal, for You are the Healer. There is no cure except Your cure, a cure that leaves behind no illness.",
            reference = "Sahih al-Bukhari 5743",
            occasion = "Visiting the sick or self-ruqyah",
            repeatCount = 3,
            benefit = "Invoke Allah's supreme attribute as As-Shafi (The Absolute Healer).",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/152.mp3"
        ),
        DuaItem(
            id = "h2",
            category = "Health & Healing",
            title = "7-Times Healing Supplication for the Sick",
            arabicText = "أَسْأَلُ اللَّهَ الْعَظِيمَ رَبَّ الْعَرْشِ الْعَظِيمِ أَنْ يَشْفِيَكَ",
            transliteration = "As'alullāhal-ʿAẓīma Rabbal-ʿArshil-ʿAẓīmi an yashfiyak",
            translation = "I ask Allah the Almighty, Lord of the Magnificent Throne, to cure you.",
            reference = "Sunan Abi Dawud 3106",
            occasion = "Recited 7 times over sick persons",
            repeatCount = 7,
            benefit = "Prophet (PBUH) promised healing for the sick person unless appointed death has come.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/148.mp3"
        ),
        DuaItem(
            id = "h3",
            category = "Health & Healing",
            title = "Hand on Pain Ruqyah (Bismillah 3x + A'udhu 7x)",
            arabicText = "بِسْمِ اللَّهِ (٣x)، أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (٧x)",
            transliteration = "Bismillāh (3x), Aʿūḏhu billāhi wa qudratihī min sharri mā ajidu wa uḥāḏhir (7x)",
            translation = "In the Name of Allah (3 times), I seek refuge in Allah and His power from the evil of what I feel and fear (7 times).",
            reference = "Sahih Muslim 2202",
            occasion = "Place right hand on location of pain",
            repeatCount = 7,
            benefit = "Subdues acute physical pain and invites restorative divine grace.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/147.mp3"
        ),
        DuaItem(
            id = "h4",
            category = "Health & Healing",
            title = "Dua of Prophet Ayyub (AS) in Chronic Illness",
            arabicText = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنتَ أَرْحَمُ الرَّاحِمِينَ",
            transliteration = "Annī massaniyaḍ-ḍurru wa anta arḥamur-rāḥimīn",
            translation = "Indeed, adversity has touched me, and You are the Most Merciful of the merciful.",
            reference = "Surah Al-Anbiya (21:83)",
            occasion = "Prolonged illness or suffering",
            repeatCount = 1,
            benefit = "A masterclass in respectful humility that unlocked miraculous recovery.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/021083.mp3"
        ),

        // --- TRAVEL & DECISIONS ---
        DuaItem(
            id = "t1",
            category = "Travel & Decisions",
            title = "Prophetic Supplication for Journey & Travel",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ، اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَٰذَا الْبِرَّ وَالتَّقْوَى",
            transliteration = "Subḥānallaḏhī sakhkhara lanā hāḏhā wa mā kunnā lahū muqrinīn, wa innā ilā Rabbinā lamunqalibūn. Allāhumma innā nas'aluka fī safarinā hāḏhal-birra wat-taqwā",
            translation = "Glory to Him who has subjected this to us, and we could never have accomplished it ourselves, and indeed to our Lord we will return. O Allah, we ask You in this journey of ours for righteousness and piety.",
            reference = "Sahih Muslim 1342",
            occasion = "Boarding vehicles, planes, or starting trips",
            repeatCount = 1,
            benefit = "Ensures angel accompaniment and divine safety throughout the voyage.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/207.mp3"
        ),
        DuaItem(
            id = "t2",
            category = "Travel & Decisions",
            title = "Supplication for Seeking Divine Counsel (Salat al-Istikhara)",
            arabicText = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ",
            transliteration = "Allāhumma innī astakhīruka biʿilmika, wa astaqdiruka biqudratika, wa as'aluka min faḍlikal-ʿaẓīm, fa'innaka taqdiru walā aqdir, wa taʿlamu walā aʿlam, wa anta ʿAllāmul-ghuyūb",
            translation = "O Allah, I seek Your guidance by virtue of Your knowledge, and I seek ability by virtue of Your power, and I ask You of Your great bounty. For indeed You are capable and I am not, and You know and I do not, and You are the Knower of the unseen.",
            reference = "Sahih al-Bukhari 1162",
            occasion = "Major decisions (marriage, work, ventures)",
            repeatCount = 1,
            benefit = "Aligns life decisions with divine blessing and removes regret.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/74.mp3"
        ),
        DuaItem(
            id = "t3",
            category = "Travel & Decisions",
            title = "Dua When Leaving the Home",
            arabicText = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Bismillāhi tawakkaltu ʿalallāhi wa lā ḥawla wa lā quwwata illā billāh",
            translation = "In the Name of Allah, I place my trust in Allah; there is no might nor power except with Allah.",
            reference = "Sunan Abi Dawud 5095",
            occasion = "Stepping out through the front door",
            repeatCount = 1,
            benefit = "It is said to him: You are guided, defended, and protected, and Satan retreats.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/16.mp3"
        ),
        DuaItem(
            id = "t4",
            category = "Travel & Decisions",
            title = "Dua Upon Entering the Home",
            arabicText = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
            transliteration = "Bismillāhi walajnā, wa bismillāhi kharajnā, wa ʿalallāhi Rabbinā tawakkalnā",
            translation = "In the Name of Allah we enter, and in the Name of Allah we leave, and upon Allah our Lord we place our trust.",
            reference = "Sunan Abi Dawud 5096",
            occasion = "Entering home with peace to family",
            repeatCount = 1,
            benefit = "Prevents Satan from lodging or partaking in food within the home.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/18.mp3"
        ),

        // --- QURANIC PROPHETIC DUAS ---
        DuaItem(
            id = "q1",
            category = "Quranic Prophetic Duas",
            title = "Dua for Goodness in Both Worlds (Rabbana Atina)",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbanā ātinā fid-dunyā ḥasanatan wa fil-ākhirati ḥasanatan wa qinā ʿaḏhāban-nār",
            translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good, and protect us from the punishment of the Fire.",
            reference = "Surah Al-Baqarah (2:201)",
            occasion = "The most frequent supplication of Prophet Muhammad (PBUH)",
            repeatCount = 3,
            benefit = "Encompasses every worldly and heavenly blessing and safeguard.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002201.mp3"
        ),
        DuaItem(
            id = "q2",
            category = "Quranic Prophetic Duas",
            title = "Supplication for Increased Knowledge & Wisdom",
            arabicText = "رَّبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidnī ʿilmā",
            translation = "My Lord, increase me in knowledge.",
            reference = "Surah Ta-Ha (20:114)",
            occasion = "Before studying or reading Quran",
            repeatCount = 3,
            benefit = "The only worldly blessing Allah commanded the Prophet to ask for an increase in.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/020114.mp3"
        ),
        DuaItem(
            id = "q3",
            category = "Quranic Prophetic Duas",
            title = "Dua for Steadfastness in Faith & Guidance",
            arabicText = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
            transliteration = "Rabbanā lā tuzigh qulūbanā baʿda iḏh hadaytanā wa hab lanā mil-ladunka raḥmah, innaka antal-Wahhāb",
            translation = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.",
            reference = "Surah Ali 'Imran (3:8)",
            occasion = "Daily spiritual anchoring",
            repeatCount = 1,
            benefit = "Guards the soul against doubt, heedlessness, and spiritual wavering.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/003008.mp3"
        ),
        DuaItem(
            id = "q4",
            category = "Quranic Prophetic Duas",
            title = "Supplication of Ibrahim (AS) for Salah & Progeny",
            arabicText = "رَبِّ اجْعَلْنِي مُقِيمَ الصَّلَاةِ وَمِن ذُرِّيَّتِي ۚ رَبَّنَا وَتَقَبَّلْ دُعَاءِ",
            transliteration = "Rabbij-ʿalnī muqīmaṣ-ṣalāti wa min ḏhurriyyatī, Rabbanā wa taqabbal duʿā'",
            translation = "My Lord, make me an establisher of prayer, and from my descendants. Our Lord, and accept my supplication.",
            reference = "Surah Ibrahim (14:40)",
            occasion = "After prayer and for family welfare",
            repeatCount = 1,
            benefit = "Brings deep Khushu to prayer and righteous guidance to children.",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/014040.mp3"
        ),
        DuaItem(
            id = "q5",
            category = "Quranic Prophetic Duas",
            title = "Dua for Parents (Mercy & Gratitude)",
            arabicText = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-ḥamhumā kamā rabbayānī ṣaghīrā",
            translation = "My Lord, have mercy upon them both as they brought me up when I was small.",
            reference = "Surah Al-Isra (17:24)",
            occasion = "For living and departed parents",
            repeatCount = 3,
            benefit = "One of the highest duties of filial piety (Birr al-Walidayn).",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/017024.mp3"
        ),
        DuaItem(
            id = "q6",
            category = "Quranic Prophetic Duas",
            title = "Dua for Joy in Spouse, Family & Leadership",
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            transliteration = "Rabbanā hab lanā min azwājinā wa ḏhurriyyātinā qurrata aʿyuniw-wajʿalnā lil-muttaqīna imāmā",
            translation = "Our Lord, grant us from among our spouses and offspring comfort to our eyes and make us an example for the righteous.",
            reference = "Surah Al-Furqan (25:74)",
            occasion = "For marital harmony and family blessing",
            repeatCount = 1,
            benefit = "Supplication of the beloved Servants of the Most Merciful (Ibad ur-Rahman).",
            audioUrl = "https://everyayah.com/data/Alafasy_128kbps/025074.mp3"
        ),
        DuaItem(
            id = "g1",
            category = "Praise & Gratitude",
            title = "The Ultimate Praise of Allah",
            arabicText = "الْحَمْدُ لِلَّهِ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ",
            transliteration = "Al-ḥamdu lillāhi ḥamdan kaṯhīran ṭayyiban mubārakan fīh",
            translation = "All praise is to Allah, praise which is abundant, pure, and full of blessings.",
            reference = "Sahih al-Bukhari 799",
            occasion = "Daily gratitude and praising Allah",
            repeatCount = 3,
            benefit = "Thirty-three angels raced with each other to write down its massive reward.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/41.mp3"
        ),
        DuaItem(
            id = "g2",
            category = "Praise & Gratitude",
            title = "Praising Allah's Unending Majesty",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "Subḥānallāhi wa biḥamdih, Subḥānallāhil-ʿAẓīm",
            translation = "Glory is to Allah and praise is to Him, Glory is to Allah the Supreme.",
            reference = "Sahih al-Bukhari 6406",
            occasion = "Constant daily remembrance",
            repeatCount = 33,
            benefit = "Two words beloved to the Most Merciful, extremely light on the tongue, but immensely heavy on the scales.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/254.mp3"
        ),
        DuaItem(
            id = "g3",
            category = "Praise & Gratitude",
            title = "Dua for Gratitude & Sincere Worship",
            arabicText = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
            transliteration = "Allāhumma aʿinnī ʿalā ḏhikrika wa shukrika wa ḥusni ʿibādatika",
            translation = "O Allah, help me to remember You, to thank You, and to worship You in the most excellent manner.",
            reference = "Sunan Abi Dawud 1522",
            occasion = "After every obligatory prayer",
            repeatCount = 3,
            benefit = "Recommended by the Prophet (PBUH) to never leave reciting after any prayer.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/59.mp3"
        ),
        DuaItem(
            id = "g4",
            category = "Praise & Gratitude",
            title = "Praise for Divine Sufficiency & Protection",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي كَفَانَا وَآوَانَا ، فَكَمْ مِمَّنْ لَا كَافِيَ لَهُ وَلَا مُؤْوِيَ",
            transliteration = "Al-ḥamdu lillāhillaḏhī kafānā wa āwānā, fakam mimman lā kāfiya lahū wa lā mu'wī",
            translation = "Praise is to Allah Who has sufficed us and sheltered us, for how many are there who have none to suffice them and none to shelter them.",
            reference = "Sahih Muslim 2715",
            occasion = "Before sleeping and in times of contentment",
            repeatCount = 1,
            benefit = "Fosters deep emotional contentment and gratitude for the basic necessities of shelter and health.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/104.mp3"
        ),
        DuaItem(
            id = "f1",
            category = "Forgiveness & Repentance",
            title = "Prophetic Prayer for Mercy & Forgiveness",
            arabicText = "اللَّهُمَّ إِنِّي ظَلَمْتُ نَفْسِي ظُلْمًا كَثِيرًا، وَلَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ، فَاغْفِرْ لِي مَغْفِرَةً مِنْ عِنْدِكَ وَارْحَمْنِي، إِنَّكَ أَنْتَ الْغَفُورُ الرَّحِيمُ",
            transliteration = "Allāhumma innī ẓalamtu nafsī ẓulman kaṯhīrā, wa lā yaghfiruḏh-ḏhunūba illā anta, faghfir lī maghfiratam-min ʿindika warḥamnī, innaka antal-Ghafūrur-Raḥīm",
            translation = "O Allah, I have indeed wronged my soul excessively, and none can forgive sins except You, so grant me forgiveness from Yourself and have mercy on me. Indeed, You are the Forgiving, the Merciful.",
            reference = "Sahih al-Bukhari 834",
            occasion = "Before the final Salam in prayer, or daily supplication",
            repeatCount = 3,
            benefit = "Taught by the Prophet (PBUH) directly to Abu Bakr (RA) to recite in his prayers.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/57.mp3"
        ),
        DuaItem(
            id = "f2",
            category = "Forgiveness & Repentance",
            title = "Short Dua for Abundant Forgiveness",
            arabicText = "رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ إِنَّكَ أَنْتَ التَّوَّابُ الرَّحِيمُ",
            transliteration = "Rabbigh-fir lī wa tub ʿalayya, innaka antat-Tawwābur-Raḥīm",
            translation = "My Lord, forgive me and accept my repentance, for indeed You are the Accepter of Repentance, the Merciful.",
            reference = "Sunan Abi Dawud 1516",
            occasion = "Recited during gatherings and daily routines",
            repeatCount = 100,
            benefit = "The Companions used to count the Prophet (PBUH) reciting this 100 times in a single sitting.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/73.mp3"
        ),
        DuaItem(
            id = "f3",
            category = "Forgiveness & Repentance",
            title = "Atonement of Any Sitting (Kaffarat al-Majlis)",
            arabicText = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ ، أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا أَنْتَ ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
            transliteration = "Subḥānakallāhumma wa biḥamdika, ash-hadu al-lā ilāha illā anta, astaghfiruka wa atūbu ilayk",
            translation = "Glory is to You, O Allah, and praise. I bear witness that there is no deity except You. I seek Your forgiveness and repent to You.",
            reference = "Sunan at-Tirmidhi 3433",
            occasion = "Before rising from any assembly, meeting, or sitting",
            repeatCount = 1,
            benefit = "Pardons and expiates whatever minor mistakes or vain talk occurred during that sitting.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/194.mp3"
        ),
        DuaItem(
            id = "f4",
            category = "Forgiveness & Repentance",
            title = "Seeking Comprehensive Forgiveness of All Sins",
            arabicText = "اللَّهُمَّ اغْفِرْ لِي ذَنْبِي كُلَّهُ: دِقَّهُ وَجِلَّهُ، وَأَوَّلَهُ وَآخِرَهُ، وَعَلَانِيَتَهُ وَسِرَّهُ",
            transliteration = "Allāhummagh-fir lī ḏhambī kullah: diqqahū wa jillah, wa awwalahū wa ākhirah, wa ʿalāniyatahū wa sirrah",
            translation = "O Allah, forgive me all my sins: the minor and the major, the first and the last, the open and the secret.",
            reference = "Sahih Muslim 483",
            occasion = "Recited in Sujud (prostration) or daily prayer",
            repeatCount = 3,
            benefit = "A comprehensive purification that leaves no trace of sin behind.",
            audioUrl = "https://www.hisnmuslim.com/audio/ar/50.mp3"
        )
    )

    val allDuas: List<DuaItem> get() = categorizedDuas

    /**
     * Search duas across title, Arabic text, translation, transliteration, category, occasion, benefit, and topical keywords.
     */
    fun search(query: String): List<DuaItem> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()

        return categorizedDuas.filter { dua ->
            dua.title.lowercase().contains(q) ||
                    dua.translation.lowercase().contains(q) ||
                    dua.arabicText.contains(q) ||
                    dua.transliteration.lowercase().contains(q) ||
                    dua.category.lowercase().contains(q) ||
                    dua.categoryAr.contains(q) ||
                    dua.occasion.lowercase().contains(q) ||
                    dua.benefit.lowercase().contains(q) ||
                    dua.reference.lowercase().contains(q) ||
                    matchesTopic(q, dua)
        }
    }

    private fun matchesTopic(q: String, dua: DuaItem): Boolean {
        val text = "${dua.title} ${dua.category} ${dua.occasion} ${dua.benefit} ${dua.translation} ${dua.reference}".lowercase()
        return when {
            q.contains("travel") || q.contains("journey") || q.contains("safar") || q.contains("flight") || q.contains("car") || q.contains("trip") || q.contains("ride") ->
                dua.category.contains("Travel", ignoreCase = true) || text.contains("travel") || text.contains("journey") || text.contains("safar")
            q.contains("anxiety") || q.contains("stress") || q.contains("worry") || q.contains("fear") || q.contains("sad") || q.contains("grief") || q.contains("distress") || q.contains("panic") ->
                dua.category.contains("Anxiety", ignoreCase = true) || dua.category.contains("Protection", ignoreCase = true) || text.contains("anxiety") || text.contains("distress") || text.contains("grief") || text.contains("worry")
            q.contains("morning") || q.contains("sabah") || q.contains("wake") || q.contains("sunrise") || q.contains("fajr") ->
                dua.category.contains("Morning", ignoreCase = true) || text.contains("morning")
            q.contains("evening") || q.contains("masa") || q.contains("night") || q.contains("sunset") || q.contains("maghrib") || q.contains("isha") ->
                dua.category.contains("Evening", ignoreCase = true) || text.contains("evening")
            q.contains("sleep") || q.contains("bed") || q.contains("insomnia") || q.contains("dream") || q.contains("awake") || q.contains("waking") ->
                dua.category.contains("Sleep", ignoreCase = true) || text.contains("sleep") || text.contains("bed") || text.contains("waking")
            q.contains("health") || q.contains("sick") || q.contains("illness") || q.contains("heal") || q.contains("cure") || q.contains("shifa") || q.contains("pain") || q.contains("ruqyah") || q.contains("fever") ->
                dua.category.contains("Health", ignoreCase = true) || text.contains("healing") || text.contains("shifa") || text.contains("sick") || text.contains("pain") || text.contains("cure")
            q.contains("forgive") || q.contains("repent") || q.contains("istighfar") || q.contains("tawbah") || q.contains("sin") || q.contains("pardon") ->
                dua.category.contains("Forgiveness", ignoreCase = true) || text.contains("forgive") || text.contains("repent") || text.contains("istighfar")
            q.contains("thank") || q.contains("shukr") || q.contains("gratitude") || q.contains("praise") || q.contains("hamd") || q.contains("blessing") ->
                dua.category.contains("Gratitude", ignoreCase = true) || dua.category.contains("Praise", ignoreCase = true) || text.contains("gratitude") || text.contains("praise") || text.contains("praising")
            q.contains("parent") || q.contains("mother") || q.contains("father") || q.contains("family") || q.contains("child") || q.contains("spouse") || q.contains("wife") || q.contains("husband") || q.contains("marriage") ->
                text.contains("parent") || text.contains("children") || text.contains("spouse") || text.contains("family") || text.contains("offspring")
            q.contains("protect") || q.contains("evil") || q.contains("hasad") || q.contains("shelter") || q.contains("refuge") || q.contains("harm") || q.contains("safety") ->
                dua.category.contains("Protection", ignoreCase = true) || text.contains("protect") || text.contains("refuge") || text.contains("shelter")
            q.contains("salah") || q.contains("prayer") || q.contains("namaz") || q.contains("prostration") || q.contains("sujud") || q.contains("tashahhud") || q.contains("salam") ->
                dua.category.contains("Salah", ignoreCase = true) || text.contains("prayer") || text.contains("salah") || text.contains("sujud")
            q.contains("decision") || q.contains("istikhara") || q.contains("guide") || q.contains("guidance") || q.contains("choice") ->
                text.contains("istikhara") || text.contains("decision") || text.contains("guidance")
            q.contains("knowledge") || q.contains("study") || q.contains("exam") || q.contains("ilm") || q.contains("wisdom") || q.contains("learn") ->
                text.contains("knowledge") || text.contains("wisdom") || text.contains("ilm") || text.contains("study")
            q.contains("rizq") || q.contains("wealth") || q.contains("money") || q.contains("debt") || q.contains("poverty") || q.contains("provision") || q.contains("sustenance") ->
                text.contains("debt") || text.contains("provision") || text.contains("sustenance") || text.contains("wealth")
            else -> false
        }
    }
}
