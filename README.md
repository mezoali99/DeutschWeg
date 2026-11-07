# Deutsch Weg 🇩🇪 — طريقك لتعلّم الألماني (A1 → B2)

تطبيق أندرويد **مجاني ومفتوح المصدر** لتعلّم اللغة الألمانية للناطقين بالعربي. بدون إعلانات، بدون حساب، ويشتغل أوفلاين.

> English: A free, open-source, offline Android app (Kotlin + Jetpack Compose) for Arabic speakers learning German from A1 to B2. Lessons live in `app/src/main/assets/content.json`, so anyone can contribute content without coding.

## المميزات
- 4 مستويات: A1 · A2 · B1 · B2 (18 وحدة في النسخة الأولى)
- شرح قواعد بالعربي لكل وحدة
- مفردات مع جمل أمثلة ونطق ألماني (من خلال محرك الصوت في جهازك)
- اختبار تلقائي لكل وحدة
- مراجعة يومية بنظام التكرار المتباعد (Leitner)
- سلسلة أيام متتالية وتتبع التقدم
- وضع ليلي وواجهة عربية (RTL)
- كل البيانات محلية على جهازك

## حالة المشروع
هذه **نسخة أولى (v0.1)**: التطبيق كامل الهيكل والوظائف، لكن المحتوى (216 كلمة) بداية فقط وليس كافيًا للوصول لمستوى B2 فعليًا. الخطة الكاملة للتوسع في [PLAN.md](PLAN.md).

> ملاحظة: الكود اتكتب بدون إمكانية تجميعه على جهاز فيه Android SDK، فممكن تحتاج تصلّح خطأ صغير عند أول Build. لو ظهر خطأ افتح Issue أو صلّحه وابعت Pull Request.

## التشغيل

### 1) Android Studio (الأسهل)
1. ثبّت Android Studio (إصدار حديث).
2. من File → Open اختار مجلد المشروع.
3. استنى الـ Gradle Sync يخلص، وبعدها Run على موبايل أو محاكي.

### 2) سطر الأوامر
```bash
# مرة واحدة فقط لتوليد gradle wrapper (محتاج Gradle 8.9+)
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
# الملف الناتج: app/build/outputs/apk/debug/app-debug.apk
```

### 3) GitHub Actions (من غير ما تنزل أي حاجة)
ارفع المشروع على GitHub، وادخل على تبويب **Actions** ← **Build APK** ← **Run workflow**. بعد دقايق هتلاقي ملف APK في الـ Artifacts تنزله وتثبته.

## المساهمة
شوف [CONTRIBUTING.md](CONTRIBUTING.md). أسهل مساهمة: إضافة وحدات أو مفردات جديدة في `content.json`.

## الرخصة
MIT — شوف ملف [LICENSE](LICENSE).
