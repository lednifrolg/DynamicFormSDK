package com.tomasovych.sdk.data

internal val formJson = """
    {
      "id": "newsletter-signup",
      "title": "Subscribe to Our Newsletter",
      "inputs": [
        {
          "id": "name",
          "title": "Full Name",
          "placeholder": "Enter your full name",
          "required": true,
          "type": "input_field",
          "keyboardType": "name"
        },
        {
          "id": "email",
          "title": "Email Address",
          "placeholder": "your.email@example.com",
          "required": true,
          "type": "input_field",
          "keyboardType": "email"
        }
      ],
      "button": {
        "text": "Join Our Mailing List"
      },
      "styling": {
        "backgroundColor": "#F5F5F5",
        "title": {
          "fontSize": 24,
          "fontColor": "#2D3142"
        },
        "input": {
          "fontColor": "#333333"
        }
      }
    }
""".trimIndent()
