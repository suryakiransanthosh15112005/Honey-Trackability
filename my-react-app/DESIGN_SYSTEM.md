# HoneyChain — Design System Specification

## 1. Brand Color System

### Primary Blue System (Trust, Security, Infrastructure)
- **Primary Main**: `#2563EB` (Tailwind `blue-600`) — Primary CTAs, active navigation items, active tab highlights, link accents.
- **Primary Deep**: `#1D4ED8` (Tailwind `blue-700`) — Hover states for primary buttons, active state markers, dark badges.
- **Primary Light / Soft Background**: `#EFF6FF` (Tailwind `blue-50`) — Informational banners, active row selections, light badge surfaces.
- **Primary Border**: `#BFDBFE` / `#DBEAFE` (Tailwind `blue-200`/`blue-100`) — Subtle borders for blue status containers and callouts.
- **Primary Text**: `#1E40AF` (Tailwind `blue-800`) — Text inside light blue containers and badges.

### Honey Brand Colors (Honey, Warmth, Quality, Origin)
- **Honey Main**: `#F59E0B` (Tailwind `amber-500`) — HoneyChain brand logo accent, rating stars, gold badge accents.
- **Honey Deep**: `#D97706` (Tailwind `amber-600`) — Honey harvest metrics, warning badges, batch weight counters.
- **Honey Light / Soft Background**: `#FEF3C7` / `#FFFBEB` (Tailwind `amber-100`/`amber-50`) — Honey status badges, batch draft highlights.
- **Honey Border**: `#FDE68A` (Tailwind `amber-200`) — Soft borders for honey-themed cards and indicators.
- **Honey Text**: `#92400E` (Tailwind `amber-800`) — Dark text on light honey surfaces.

### Neutral System (Backgrounds, Surfaces, Text)
- **Page Background**: `#F8FAFC` (Tailwind `slate-50`) — Clean, modern, bright background for all page viewports.
- **Card / Container Surface**: `#FFFFFF` (Tailwind `white`) — Primary elevated surface with `shadow-sm` and `border-slate-200`.
- **Subtle Surface**: `#F1F5F9` (Tailwind `slate-100`) — Table header backgrounds, input field backgrounds, code snippets.
- **Primary Text**: `#0F172A` / `#1E293B` (Tailwind `slate-900`/`slate-800`) — High-contrast body text and headings.
- **Secondary Text**: `#64748B` / `#475569` (Tailwind `slate-500`/`slate-600`) — Subtitles, captions, metadata labels.
- **Border / Divider**: `#E2E8F0` (Tailwind `slate-200`) — Standard border for cards, inputs, and section dividers.

---

## 2. Status Color Mappings

All status indicators across IoT, Lab Testing, Batch Lifecycle, and Risk Verification are strictly mapped into the Blue + Honey + Neutral palette:

| Status Category | Status Value | Badge Surface | Text / Icon Color | Border Color |
| :--- | :--- | :--- | :--- | :--- |
| **Purity / Quality** | PURE / PASS / Normal | `#EFF6FF` (`blue-50`) | `#1D4ED8` (`blue-700`) | `#BFDBFE` (`blue-200`) |
| **Pending / Standard** | UNDER_REVIEW / PENDING / Watch | `#FEF3C7` (`amber-100`) | `#D97706` (`amber-600`) | `#FDE68A` (`amber-200`) |
| **Alert / High Risk** | HIGH_RISK / FAILED / Critical | `#EFF6FF` (`blue-50`) | `#1E40AF` (`blue-800`) + Bold | `#93C5FD` (`blue-300`) |
| **Inactive / Draft** | INACTIVE / CREATED / Offline | `#F1F5F9` (`slate-100`) | `#64748B` (`slate-500`) | `#E2E8F0` (`slate-200`) |

---

## 3. Typography & Spacing Rules

- **Font Family**: Modern sans-serif (`Inter`, `Roboto`, `Outfit`) with `font-mono` for IDs, hashes, and quantitative metrics.
- **Headings**: `font-bold text-slate-900 font-['Outfit']`.
- **Card Spacing**: `p-5` or `p-6` with `rounded-2xl border border-slate-200 bg-white shadow-sm`.
- **Buttons**: `rounded-xl font-semibold px-4 py-2.5 transition-all shadow-sm`.
