package com.indiacybercafe.quicktools

object ToolRepository {

    fun getCategories(): List<Category> {
        return listOf(
            Category("PDF Tools", R.drawable.ic_pdf),
            Category("Image Tools", R.drawable.ic_image),
            Category("Document Tools", R.drawable.ic_document),
            Category("Text Tools", R.drawable.ic_text),
            Category("Calculator", R.drawable.ic_calculator),
            Category("Converters", R.drawable.ic_converter),
            Category("Security & Privacy", R.drawable.ic_security),
            Category("QR & Barcode", R.drawable.ic_qr),
            Category("Web & URL Tools", R.drawable.ic_web),
            Category("Color & Design", R.drawable.ic_color),
            Category("Developer Tools", R.drawable.ic_developer),
            Category("Date & Time", R.drawable.ic_date),
            Category("Finance Tools", R.drawable.ic_finance),
            Category("Math Tools", R.drawable.ic_math),
            Category("Social Media Tools", R.drawable.ic_social),
            Category("Audio Tools", R.drawable.ic_audio),
            Category("Video Tools", R.drawable.ic_video),
            Category("File Tools", R.drawable.ic_file),
            Category("Data Tools", R.drawable.ic_data),
            Category("Miscellaneous Tools", R.drawable.ic_misc),
            Category("Cyber Cafe Tools", R.drawable.ic_studio)
        )
    }

    fun getToolsByCategory(categoryName: String): List<Category> {
        return when (categoryName) {
            "PDF Tools" -> listOf(
                Category("Merge PDF", R.drawable.ic_merge),
                Category("Split PDF", R.drawable.ic_split),
                Category("Compress PDF", R.drawable.ic_compress),
                Category("PDF to JPG", R.drawable.ic_image),
                Category("JPG to PDF", R.drawable.ic_pdf),
                Category("PDF to Word", R.drawable.ic_word),
                Category("Word to PDF", R.drawable.ic_pdf),
                Category("PDF to Excel", R.drawable.ic_excel),
                Category("Excel to PDF", R.drawable.ic_pdf),
                Category("Rotate PDF", R.drawable.ic_converter),
                Category("Protect PDF", R.drawable.ic_lock),
                Category("Unlock PDF", R.drawable.ic_unlock)
            )
            "Image Tools" -> listOf(
                Category("Compress Image", R.drawable.ic_compress),
                Category("Resize Image", R.drawable.ic_resize),
                Category("Crop Image", R.drawable.ic_crop),
                Category("JPG to PNG", R.drawable.ic_image),
                Category("PNG to JPG", R.drawable.ic_image),
                Category("WebP Converter", R.drawable.ic_converter),
                Category("Image to PDF", R.drawable.ic_pdf),
                Category("Image Converter", R.drawable.ic_converter),
                Category("Rotate Image", R.drawable.ic_converter),
                Category("Image Watermark", R.drawable.ic_image),
                Category("Remove Image Background", R.drawable.ic_image),
                Category("Image Metadata Remover", R.drawable.ic_trash)
            )
            "Document Tools" -> listOf(
                Category("Word to PDF", R.drawable.ic_pdf),
                Category("PDF to Word", R.drawable.ic_word),
                Category("Excel to PDF", R.drawable.ic_pdf),
                Category("PDF to Excel", R.drawable.ic_excel),
                Category("PPT to PDF", R.drawable.ic_pdf),
                Category("PDF to PPT", R.drawable.ic_ppt),
                Category("TXT to PDF", R.drawable.ic_pdf),
                Category("CSV to PDF", R.drawable.ic_pdf),
                Category("Document Converter", R.drawable.ic_converter),
                Category("Document Compressor", R.drawable.ic_compress)
            )
            "Text Tools" -> listOf(
                Category("Word Counter", R.drawable.ic_count),
                Category("Character Counter", R.drawable.ic_count),
                Category("Case Converter", R.drawable.ic_text),
                Category("Remove Extra Spaces", R.drawable.ic_trash),
                Category("Remove Duplicate Lines", R.drawable.ic_trash),
                Category("Sort Text", R.drawable.ic_text),
                Category("Reverse Text", R.drawable.ic_text),
                Category("Text to PDF", R.drawable.ic_pdf),
                Category("Text to Image", R.drawable.ic_image),
                Category("Lorem Ipsum Generator", R.drawable.ic_text)
            )
            "Calculator" -> listOf(
                Category("Calculator", R.drawable.ic_calculator),
                Category("Age Calculator", R.drawable.ic_date),
                Category("Percentage Calculator", R.drawable.ic_percent),
                Category("GST Calculator", R.drawable.ic_money),
                Category("Discount Calculator", R.drawable.ic_percent),
                Category("EMI Calculator", R.drawable.ic_finance),
                Category("Simple Interest", R.drawable.ic_money),
                Category("Compound Interest", R.drawable.ic_money),
                Category("Tip Calculator", R.drawable.ic_money),
                Category("Loan Calculator", R.drawable.ic_finance)
            )
            "Converters" -> listOf(
                Category("Unit Converter", R.drawable.ic_converter),
                Category("Currency Converter", R.drawable.ic_money),
                Category("Length Converter", R.drawable.ic_resize),
                Category("Weight Converter", R.drawable.ic_converter),
                Category("Temperature Converter", R.drawable.ic_converter),
                Category("Area Converter", R.drawable.ic_converter),
                Category("Volume Converter", R.drawable.ic_converter),
                Category("Speed Converter", R.drawable.ic_speed),
                Category("Time Converter", R.drawable.ic_timer),
                Category("Number to Words", R.drawable.ic_translate)
            )
            "Security & Privacy" -> listOf(
                Category("Password Generator", R.drawable.ic_lock),
                Category("Strong Password Checker", R.drawable.ic_security),
                Category("Hash Generator", R.drawable.ic_lock),
                Category("MD5 Generator", R.drawable.ic_lock),
                Category("SHA Generator", R.drawable.ic_lock),
                Category("File Encrypt", R.drawable.ic_lock),
                Category("File Decrypt", R.drawable.ic_unlock),
                Category("Metadata Viewer", R.drawable.ic_security),
                Category("Metadata Remover", R.drawable.ic_trash),
                Category("Secure Random Generator", R.drawable.ic_security)
            )
            "QR & Barcode" -> listOf(
                Category("QR Code Generator", R.drawable.ic_qr),
                Category("QR Code Scanner", R.drawable.ic_scan),
                Category("Barcode Generator", R.drawable.ic_scan),
                Category("Barcode Scanner", R.drawable.ic_scan),
                Category("Wi-Fi QR Generator", R.drawable.ic_qr),
                Category("UPI QR Generator", R.drawable.ic_qr),
                Category("URL QR Generator", R.drawable.ic_qr),
                Category("Text QR Generator", R.drawable.ic_qr),
                Category("Email QR Generator", R.drawable.ic_qr),
                Category("Contact QR Generator", R.drawable.ic_qr)
            )
            "Web & URL Tools" -> listOf(
                Category("URL Shortener", R.drawable.ic_link),
                Category("URL Encoder", R.drawable.ic_link),
                Category("URL Decoder", R.drawable.ic_link),
                Category("Website Screenshot", R.drawable.ic_web),
                Category("Website QR Generator", R.drawable.ic_qr),
                Category("HTML Viewer", R.drawable.ic_code),
                Category("JSON Formatter", R.drawable.ic_data),
                Category("JSON Validator", R.drawable.ic_data),
                Category("IP Address Lookup", R.drawable.ic_web),
                Category("User Agent Checker", R.drawable.ic_web)
            )
            "Color & Design" -> listOf(
                Category("Color Picker", R.drawable.ic_palette),
                Category("HEX to RGB", R.drawable.ic_color),
                Category("RGB to HEX", R.drawable.ic_color),
                Category("HEX to HSL", R.drawable.ic_color),
                Category("Color Converter", R.drawable.ic_converter),
                Category("Gradient Generator", R.drawable.ic_palette),
                Category("Color Palette Generator", R.drawable.ic_palette),
                Category("Image Color Extractor", R.drawable.ic_color),
                Category("Contrast Checker", R.drawable.ic_color),
                Category("Random Color Generator", R.drawable.ic_color)
            )
            "Developer Tools" -> listOf(
                Category("JSON Formatter", R.drawable.ic_code),
                Category("JSON Validator", R.drawable.ic_code),
                Category("Base64 Encoder", R.drawable.ic_code),
                Category("Base64 Decoder", R.drawable.ic_code),
                Category("URL Encoder", R.drawable.ic_link),
                Category("URL Decoder", R.drawable.ic_link),
                Category("HTML Formatter", R.drawable.ic_code),
                Category("CSS Formatter", R.drawable.ic_code),
                Category("JavaScript Formatter", R.drawable.ic_code),
                Category("Regex Tester", R.drawable.ic_code),
                Category("Timestamp Converter", R.drawable.ic_timer),
                Category("UUID Generator", R.drawable.ic_developer)
            )
            "Date & Time" -> listOf(
                Category("Age Calculator", R.drawable.ic_date),
                Category("Date Difference", R.drawable.ic_date),
                Category("Days Calculator", R.drawable.ic_date),
                Category("Add Days to Date", R.drawable.ic_date),
                Category("Subtract Days from Date", R.drawable.ic_date),
                Category("Time Zone Converter", R.drawable.ic_timer),
                Category("Unix Timestamp Converter", R.drawable.ic_timer),
                Category("Stopwatch", R.drawable.ic_timer),
                Category("Countdown Timer", R.drawable.ic_timer),
                Category("World Clock", R.drawable.ic_timer)
            )
            "Finance Tools" -> listOf(
                Category("GST Calculator", R.drawable.ic_money),
                Category("EMI Calculator", R.drawable.ic_finance),
                Category("Loan Calculator", R.drawable.ic_finance),
                Category("Simple Interest", R.drawable.ic_money),
                Category("Compound Interest", R.drawable.ic_money),
                Category("Discount Calculator", R.drawable.ic_percent),
                Category("Percentage Calculator", R.drawable.ic_percent),
                Category("Profit & Loss Calculator", R.drawable.ic_finance),
                Category("Investment Calculator", R.drawable.ic_finance),
                Category("Tax Calculator", R.drawable.ic_money)
            )
            "Math Tools" -> listOf(
                Category("Scientific Calculator", R.drawable.ic_math),
                Category("Percentage Calculator", R.drawable.ic_percent),
                Category("Fraction Calculator", R.drawable.ic_math),
                Category("Average Calculator", R.drawable.ic_math),
                Category("Ratio Calculator", R.drawable.ic_math),
                Category("LCM Calculator", R.drawable.ic_math),
                Category("HCF Calculator", R.drawable.ic_math),
                Category("Prime Number Checker", R.drawable.ic_math),
                Category("Square Root Calculator", R.drawable.ic_math),
                Category("Power Calculator", R.drawable.ic_math)
            )
            "Social Media Tools" -> listOf(
                Category("Instagram Post Downloader", R.drawable.ic_download),
                Category("Instagram Profile Picture Downloader", R.drawable.ic_download),
                Category("YouTube Thumbnail Downloader", R.drawable.ic_download),
                Category("YouTube Title Extractor", R.drawable.ic_text),
                Category("Hashtag Generator", R.drawable.ic_social),
                Category("Caption Generator", R.drawable.ic_text),
                Category("Social Media Image Resizer", R.drawable.ic_resize),
                Category("Profile Picture Resizer", R.drawable.ic_resize),
                Category("Post Image Resizer", R.drawable.ic_resize),
                Category("Social Media QR Generator", R.drawable.ic_qr)
            )
            "Audio Tools" -> listOf(
                Category("Audio Converter", R.drawable.ic_converter),
                Category("MP3 Converter", R.drawable.ic_audio),
                Category("Audio Compressor", R.drawable.ic_compress),
                Category("Audio Cutter", R.drawable.ic_audio),
                Category("Audio Merger", R.drawable.ic_audio),
                Category("Audio Trimmer", R.drawable.ic_audio),
                Category("Audio to Text", R.drawable.ic_translate),
                Category("Text to Speech", R.drawable.ic_audio),
                Category("Extract Audio from Video", R.drawable.ic_video),
                Category("Change Audio Speed", R.drawable.ic_speed)
            )
            "Video Tools" -> listOf(
                Category("Video Converter", R.drawable.ic_converter),
                Category("Video Compressor", R.drawable.ic_compress),
                Category("Video Cutter", R.drawable.ic_video),
                Category("Video Trimmer", R.drawable.ic_video),
                Category("Video Merger", R.drawable.ic_video),
                Category("Video to MP3", R.drawable.ic_audio),
                Category("Video to GIF", R.drawable.ic_video),
                Category("Video to JPG", R.drawable.ic_image),
                Category("Resize Video", R.drawable.ic_resize),
                Category("Change Video Speed", R.drawable.ic_speed)
            )
            "File Tools" -> listOf(
                Category("File Compressor", R.drawable.ic_compress),
                Category("ZIP Creator", R.drawable.ic_zip),
                Category("ZIP Extractor", R.drawable.ic_zip),
                Category("File Converter", R.drawable.ic_converter),
                Category("File Renamer", R.drawable.ic_file),
                Category("File Size Checker", R.drawable.ic_file),
                Category("File Type Converter", R.drawable.ic_converter),
                Category("Merge Files", R.drawable.ic_file),
                Category("Split File", R.drawable.ic_file),
                Category("File Information", R.drawable.ic_file)
            )
            "Data Tools" -> listOf(
                Category("JSON Formatter", R.drawable.ic_code),
                Category("JSON Validator", R.drawable.ic_code),
                Category("CSV Viewer", R.drawable.ic_data),
                Category("CSV to JSON", R.drawable.ic_data),
                Category("JSON to CSV", R.drawable.ic_data),
                Category("XML Formatter", R.drawable.ic_code),
                Category("XML Validator", R.drawable.ic_code),
                Category("Base64 Encoder", R.drawable.ic_code),
                Category("Base64 Decoder", R.drawable.ic_code),
                Category("Data Converter", R.drawable.ic_converter)
            )
            "Miscellaneous Tools" -> listOf(
                Category("Random Number Generator", R.drawable.ic_misc),
                Category("Random Password Generator", R.drawable.ic_lock),
                Category("UUID Generator", R.drawable.ic_developer),
                Category("QR Code Generator", R.drawable.ic_qr),
                Category("Barcode Generator", R.drawable.ic_scan),
                Category("Stopwatch", R.drawable.ic_timer),
                Category("Countdown Timer", R.drawable.ic_timer),
                Category("Coin Flip", R.drawable.ic_misc),
                Category("Dice Roller", R.drawable.ic_misc),
                Category("Lorem Ipsum Generator", R.drawable.ic_text)
            )
            "Cyber Cafe Tools" -> listOf(
                Category("Passport Photo", R.drawable.ic_passport),
                Category("PVC Card Studio", R.drawable.ic_pvc),
                Category("Multi-Page Studio", R.drawable.ic_studio)
            )
            else -> emptyList()
        }
    }

    fun getTopTools(): List<Category> {
        val cyberCafeTools = getToolsByCategory("Cyber Cafe Tools")
        val imageTools = getToolsByCategory("Image Tools")
        
        return listOf(
            cyberCafeTools.find { it.name == "Passport Photo" }!!,
            cyberCafeTools.find { it.name == "Multi-Page Studio" }!!,
            imageTools.find { it.name == "Remove Image Background" }!!
        )
    }
}