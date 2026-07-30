---
name: Design system
typography:
  display-lg:
    fontFamily: Roboto Flex
    fontSize: 56px
    fontWeight: '800'
    lineHeight: 64px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Roboto Flex
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Roboto Flex
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  title-lg:
    fontFamily: Roboto Flex
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Roboto Flex
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
    letterSpacing: 0.01em
  body-md:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.05em
  label-md:
    fontFamily: Roboto Flex
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  container-padding-desktop: 40px
  container-padding-mobile: 20px
  gutter: 24px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style
The design system is a sophisticated, reading-centric interface optimized for deep focus and long-form consumption. It targets a literate, tech-savvy audience that values editorial quality and eye comfort. The style is **Modern Minimalist** with a focus on **Tonal Layering**, utilizing a deep charcoal foundation to minimize eye strain while highlighting content with a warm, inviting peach accent. The emotional response is one of calm, intellectual immersion and premium craftsmanship.

## Colors
This design system utilizes a "Deep Charcoal" (#2C2C42) as the core environmental color to create a low-light reading sanctuary. The "Vibrant Peach" (#FFB894) serves as the primary action color, providing high-energy contrast against the dark surfaces without the harshness of pure white or neon tones. 

Surface levels are defined by subtle increases in lightness rather than traditional shadows:
- **Background:** The base layer for the application.
- **Surface:** Used for cards and elevated reading areas.
- **Surface Variant:** Used for secondary UI elements like navigation bars and search inputs.
- **Contrast:** Text colors are strictly enforced at high-contrast ratios (WCAG AAA for body text) to ensure legibility in various lighting conditions.

### ☀️ Light Theme (Ebony & Peach Reader)
- **Surface**: `#f9f9f9` (Main background)
- **Surface Dim**: `#dadada`
- **Surface Bright**: `#f9f9f9`
- **Surface Container Lowest**: `#ffffff`
- **Surface Container Low**: `#f3f3f4`
- **Surface Container**: `#eeeeef`
- **Surface Container High**: `#e8e8e9`
- **Surface Container Highest**: `#e2e2e3`
- **Primary**: `#2c2c42` (Ebony/Charcoal)
- **On Primary**: `#ffffff`
- **Secondary**: `#ffb894` (Peach Accent)
- **On Secondary**: `#2c2c42`

### 🌙 Dark Theme (Nocturne Narrative)
- **Surface**: `#13131b` (Deep Charcoal background)
- **Surface Dim**: `#13131b`
- **Surface Bright**: `#393841`
- **Surface Container Lowest**: `#0d0d15`
- **Surface Container Low**: `#1b1b23`
- **Surface Container**: `#21212a`
- **Surface Container High**: `#2b2b34`
- **Surface Container Highest**: `#36363f`
- **Primary**: `#ffb894` (Peach Accent as primary in dark mode)
- **On Primary**: `#13131b`
- **Secondary**: `#2c2c42`
- **On Secondary**: `#ffffff`

## Typography
The design system relies exclusively on **Roboto Flex** for its exceptional versatility in a digital reading context. By utilizing variable axes, we maintain a consistent visual language while differentiating hierarchy through weight and grade.

- **Editorial Focus:** Body-lg is optimized for long-form reading with a generous line height and slight positive tracking to improve character recognition on dark backgrounds.
- **Headlines:** Use tighter tracking and heavier weights to anchor the page.
- **Labels:** Uppercase styles are used for navigation and small metadata to provide a distinct visual break from the narrative text.

## Layout & Spacing
The layout follows an **8px grid system**, ensuring mathematical harmony across all components.

- **Reading Container:** On desktop, the primary reading container is constrained to a maximum width of 720px to maintain optimal line lengths (approx. 65-75 characters).
- **Fluidity:** Gutters and margins scale from 20px on mobile to 40px on large desktops.
- **Vertical Rhythm:** Elements are separated by "Stack" units (8, 16, 32, 64) to maintain a consistent vertical flow that feels intentional and spacious.

## Elevation & Depth
In this dark-mode system, elevation is conveyed through **Tonal Luminance** rather than heavy shadows. As an element moves "closer" to the user, its surface color becomes lighter.

- **Level 0 (Base):** Background (#2C2C42).
- **Level 1 (Cards/Sheet):** Surface (#36364D).
- **Level 2 (Dialogs/Popovers):** Surface Variant (#42425E) with a very subtle, 10% opacity peach-tinted glow (Blur: 12px, Y: 4px).
- **Outlines:** Low-contrast borders (1px solid #42425E) are used on inputs and buttons to define boundaries without adding visual noise.

## Shapes
The design system uses a **Rounded** philosophy (0.5rem base) to soften the technical feel of the charcoal palette. This creates a "friendly-modern" aesthetic. Large containers like cards or bottom sheets should utilize the `rounded-xl` (1.5rem) token to emphasize the containerized nature of the reading experience.

## Components
- **Buttons:** Primary buttons use the Vibrant Peach background with #12121A text for maximum impact. Secondary buttons use an outline of the Surface Variant with Peach text.
- **Chips:** Used for genre tags or categories. Background: Surface Variant; Text: On-Surface-Variant; Rounded: Pill-shaped.
- **Input Fields:** Background: Background (Sunken); Border: Surface Variant; Active Border: Primary (Peach).
- **Cards:** No shadow. Background: Surface. 1px border using Surface Variant to define the edge against the Background.
- **Reading Progress:** A thin 2px bar at the top of the viewport in Vibrant Peach to track reading completion.
- **Selection:** Text selection should use the Primary color at 30% opacity with the On-Surface text remaining white.