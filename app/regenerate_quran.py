import urllib.request
import json
import os

canonical_meta = [
    (1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
    (2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan"),
    (3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan"),
    (4, "النساء", "An-Nisa", "The Women", 176, "Medinan"),
    (5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, "Medinan"),
    (6, "الأنعام", "Al-An'am", "The Cattle", 165, "Meccan"),
    (7, "الأعراف", "Al-A'raf", "The Heights", 206, "Meccan"),
    (8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, "Medinan"),
    (9, "التوبة", "At-Tawbah", "The Repentance", 129, "Medinan"),
    (10, "يونس", "Yunus", "Jonah", 109, "Meccan"),
    (11, "هود", "Hud", "Hud", 123, "Meccan"),
    (12, "يوسف", "Yusuf", "Joseph", 111, "Meccan"),
    (13, "الرعد", "Ar-Ra'd", "The Thunder", 43, "Medinan"),
    (14, "إبراهيم", "Ibrahim", "Abraham", 52, "Meccan"),
    (15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, "Meccan"),
    (16, "النحل", "An-Nahl", "The Bee", 128, "Meccan"),
    (17, "الإسراء", "Al-Isra", "The Night Journey", 111, "Meccan"),
    (18, "الكهف", "Al-Kahf", "The Cave", 110, "Meccan"),
    (19, "مريم", "Maryam", "Mary", 98, "Meccan"),
    (20, "طه", "Ta-Ha", "Ta-Ha", 135, "Meccan"),
    (21, "الأنبيآء", "Al-Anbiya", "The Prophets", 112, "Meccan"),
    (22, "الحج", "Al-Hajj", "The Pilgrimage", 78, "Medinan"),
    (23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, "Meccan"),
    (24, "النور", "An-Nur", "The Light", 64, "Medinan"),
    (25, "الفرقان", "Al-Furqan", "The Criterion", 77, "Meccan"),
    (26, "الشعراء", "Ash-Shu'ara", "The Poets", 227, "Meccan"),
    (27, "النمل", "An-Naml", "The Ant", 93, "Meccan"),
    (28, "القصص", "Al-Qasas", "The Stories", 88, "Meccan"),
    (29, "العنكبوت", "Al-'Ankabut", "The Spider", 69, "Meccan"),
    (30, "الروم", "Ar-Rum", "The Romans", 60, "Meccan"),
    (31, "لقمان", "Luqman", "Luqman", 34, "Meccan"),
    (32, "السجدة", "As-Sajdah", "The Prostration", 30, "Meccan"),
    (33, "الأحزاب", "Al-Ahzab", "The Combined Forces", 73, "Medinan"),
    (34, "سبإ", "Saba", "Sheba", 54, "Meccan"),
    (35, "فاطر", "Fatir", "Originator", 45, "Meccan"),
    (36, "يس", "Ya-Sin", "Ya-Sin", 83, "Meccan"),
    (37, "الصافات", "As-Saffat", "Those who set the Ranks", 182, "Meccan"),
    (38, "ص", "Sad", "The Letter Sad", 88, "Meccan"),
    (39, "الزمر", "Az-Zumar", "The Troops", 75, "Meccan"),
    (40, "غافر", "Ghafir", "The Forgiver", 85, "Meccan"),
    (41, "فصلت", "Fussilat", "Explained in Detail", 54, "Meccan"),
    (42, "الشورى", "Ash-Shura", "The Consultation", 53, "Meccan"),
    (43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", 89, "Meccan"),
    (44, "الدخان", "Ad-Dukhan", "The Smoke", 59, "Meccan"),
    (45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, "Meccan"),
    (46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, "Meccan"),
    (47, "محمد", "Muhammad", "Muhammad", 38, "Medinan"),
    (48, "الفتح", "Al-Fath", "The Victory", 29, "Medinan"),
    (49, "الحجرات", "Al-Hujurat", "The Rooms", 18, "Medinan"),
    (50, "ق", "Qaf", "The Letter Qaf", 45, "Meccan"),
    (51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", 60, "Meccan"),
    (52, "الطور", "At-Tur", "The Mount", 49, "Meccan"),
    (53, "النجم", "An-Najm", "The Star", 62, "Meccan"),
    (54, "القمر", "Al-Qamar", "The Moon", 55, "Meccan"),
    (55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Medinan"),
    (56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, "Meccan"),
    (57, "الحديد", "Al-Hadid", "The Iron", 29, "Medinan"),
    (58, "المجادلة", "Al-Mujadila", "The Pleading Woman", 22, "Medinan"),
    (59, "الحشر", "Al-Hashr", "The Exile", 24, "Medinan"),
    (60, "الممتحنة", "Al-Mumtahanah", "She that is to be examined", 13, "Medinan"),
    (61, "الصف", "As-Saff", "The Ranks", 14, "Medinan"),
    (62, "الجمعة", "Al-Jumu'ah", "Friday", 11, "Medinan"),
    (63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, "Medinan"),
    (64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, "Medinan"),
    (65, "الطلاق", "At-Talaq", "The Divorce", 12, "Medinan"),
    (66, "التحريم", "At-Tahrim", "The Prohibition", 12, "Medinan"),
    (67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Meccan"),
    (68, "القلم", "Al-Qalam", "The Pen", 52, "Meccan"),
    (69, "الحاقة", "Al-Haqqah", "The Reality", 52, "Meccan"),
    (70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, "Meccan"),
    (71, "نوح", "Nuh", "Noah", 28, "Meccan"),
    (72, "الجن", "Al-Jinn", "The Jinn", 28, "Meccan"),
    (73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, "Meccan"),
    (74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, "Meccan"),
    (75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, "Meccan"),
    (76, "الإنسان", "Al-Insan", "Man", 31, "Medinan"),
    (77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, "Meccan"),
    (78, "النبأ", "An-Naba", "The Tidings", 40, "Meccan"),
    (79, "النازعات", "An-Nazi'at", "Those who drag forth", 46, "Meccan"),
    (80, "عبس", "'Abasa", "He Frowned", 42, "Meccan"),
    (81, "التكوير", "At-Takwir", "The Overthrowing", 29, "Meccan"),
    (82, "الانفطار", "Al-Infitar", "The Cleaving", 19, "Meccan"),
    (83, "المطففين", "Al-Mutaffifin", "Defrauding", 36, "Meccan"),
    (84, "الانشقاق", "Al-Inshiqaq", "The Splitting Open", 25, "Meccan"),
    (85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, "Meccan"),
    (86, "الطارق", "At-Tariq", "The Nightcommer", 17, "Meccan"),
    (87, "الأعلى", "Al-A'la", "The Most High", 19, "Meccan"),
    (88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, "Meccan"),
    (89, "الفجر", "Al-Fajr", "The Dawn", 30, "Meccan"),
    (90, "البلد", "Al-Balad", "The City", 20, "Meccan"),
    (91, "الشمس", "Ash-Shams", "The Sun", 15, "Meccan"),
    (92, "الليل", "Al-Layl", "The Night", 21, "Meccan"),
    (93, "الضحى", "Ad-Duha", "The Morning Hours", 11, "Meccan"),
    (94, "الشرح", "Ash-Sharh", "The Relief", 8, "Meccan"),
    (95, "التين", "At-Tin", "The Fig", 8, "Meccan"),
    (96, "العلق", "Al-'Alaq", "The Clot", 19, "Meccan"),
    (97, "القدر", "Al-Qadr", "The Power", 5, "Meccan"),
    (98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, "Medinan"),
    (99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, "Medinan"),
    (100, "العاديات", "Al-'Adiyat", "The Courser", 11, "Meccan"),
    (101, "القارعة", "Al-Qari'ah", "The Calamity", 11, "Meccan"),
    (102, "التكاثر", "At-Takathur", "The Rivalry in world increase", 8, "Meccan"),
    (103, "العصر", "Al-'Asr", "The Declining Day", 3, "Meccan"),
    (104, "الهمزة", "Al-Humazah", "The Traducer", 9, "Meccan"),
    (105, "الفيل", "Al-Fil", "The Elephant", 5, "Meccan"),
    (106, "قريش", "Quraysh", "Quraysh", 4, "Meccan"),
    (107, "الماعون", "Al-Ma'un", "The Small Kindness", 7, "Meccan"),
    (108, "الكوثر", "Al-Kawthar", "The Abundance", 3, "Meccan"),
    (109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, "Meccan"),
    (110, "النصر", "An-Nasr", "The Divine Support", 3, "Medinan"),
    (111, "المسد", "Al-Masad", "The Palm Fiber", 5, "Meccan"),
    (112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Meccan"),
    (113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Meccan"),
    (114, "الناس", "An-Nas", "Mankind", 6, "Meccan")
]

def fetch(url):
    req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
    with urllib.request.urlopen(req, timeout=120) as resp:
        return json.loads(resp.read().decode("utf-8"))

print("Fetching editions from api.alquran.cloud...")
uthmani_raw = fetch("https://api.alquran.cloud/v1/quran/quran-uthmani")
translation_raw = fetch("https://api.alquran.cloud/v1/quran/en.sahih")
transliteration_raw = fetch("https://api.alquran.cloud/v1/quran/en.transliteration")

out_dir = "app/src/main/assets/quran"
os.makedirs(out_dir, exist_ok=True)

# Write raw edition assets
print("Writing raw assets...")
with open(os.path.join(out_dir, "quran_ar_uthmani.json"), "w", encoding="utf-8") as f:
    json.dump(uthmani_raw, f, ensure_ascii=False)

with open(os.path.join(out_dir, "quran_en_translation.json"), "w", encoding="utf-8") as f:
    json.dump(translation_raw, f, ensure_ascii=False)

with open(os.path.join(out_dir, "quran_en_transliteration.json"), "w", encoding="utf-8") as f:
    json.dump(transliteration_raw, f, ensure_ascii=False)

uthmani = uthmani_raw["data"]["surahs"]
translation = translation_raw["data"]["surahs"]
transliteration = transliteration_raw["data"]["surahs"]

complete_list = []
abs_num = 0

for idx, meta in enumerate(canonical_meta):
    s_num, name_ar, name_en, meaning, total_v, rev_type = meta
    u_surah = uthmani[idx]
    t_surah = translation[idx]
    tr_surah = transliteration[idx]
    
    u_ayahs = u_surah["ayahs"]
    t_ayahs = t_surah["ayahs"]
    tr_ayahs = tr_surah["ayahs"]
    
    verses_list = []
    for j in range(len(u_ayahs)):
        abs_num += 1
        u_a = u_ayahs[j]
        t_a = t_ayahs[j]
        tr_a = tr_ayahs[j]
        
        v_obj = {
            "surahNumber": s_num,
            "verseNumber": u_a["numberInSurah"],
            "absoluteNumber": abs_num,
            "juz": u_a["juz"],
            "page": u_a["page"],
            "arabicText": u_a["text"],
            "transliteration": tr_a["text"],
            "translation": t_a["text"],
            "tafsirShort": ""
        }
        verses_list.append(v_obj)
        
    surah_obj = {
        "number": s_num,
        "nameEnglish": name_en,
        "nameArabic": name_ar,
        "englishMeaning": meaning,
        "totalVerses": len(verses_list),
        "revelationType": rev_type,
        "verses": verses_list
    }
    complete_list.append(surah_obj)

complete_path = os.path.join(out_dir, "quran_complete.json")
print("Writing", complete_path)
with open(complete_path, "w", encoding="utf-8") as f:
    json.dump(complete_list, f, ensure_ascii=False, indent=2)

print(f"Successfully generated all 4 files! Surahs: {len(complete_list)}, Total verses: {abs_num}")
