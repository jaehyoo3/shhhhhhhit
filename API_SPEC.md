# 📋 Foorend API 명세서

> 최종 업데이트: 2025-11-28

---

## 1. 사용자 (User)

### 1.1 기본정보 입력
```
POST /api/user/basic-info
```
| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| name | String | ✅ | 이름 |
| phoneNumber | String | | 핸드폰 번호 |
| gender | Enum | ✅ | MALE / FEMALE |
| birthday | LocalDate | ✅ | 생년월일 (yyyy-MM-dd) |
| relationshipStatus | Enum | | 연애 상태 |
| nationality | String | | 국적 코드 (KR, US 등) |
| jobCategory | Enum | | 직업 카테고리 |
| traitsAnswers | Object | | 성향 답변 (JSON) |

---

### 1.2 프로필 수정 (PATCH)
```
PATCH /api/user/profile
```
| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| name | String | | 이름 |
| phoneNumber | String | | 핸드폰 번호 |
| relationshipStatus | Enum | | 연애 상태 |
| jobCategory | Enum | | 직업 카테고리 |
| nationality | String | | 국적 |

> ※ 보낸 값만 업데이트됨 (null은 무시)

---

### 1.3 내 프로필 조회
```
GET /api/user/profile
```
**Response**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "email": "user@gmail.com",
    "name": "홍길동",
    "phoneNumber": "01012345678",
    "gender": "MALE",
    "birthday": "1995-05-15",
    "profileImageUrl": "https://...",
    "relationshipStatus": "SINGLE",
    "nationality": "KR",
    "jobCategory": "TECH",
    "traitsAnswers": { "q1": "A", "q2": "B" }
  }
}
```

---

### 1.4 회원 탈퇴
```
DELETE /api/user
```
| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| confirmText | String | ✅ | "DELETE" 입력 필요 |
| reason | String | | 탈퇴 사유 |

---

### 1.5 로그아웃
```
POST /api/user/logout
```
> Body 없음

---

## 2. 선호 설정 (Preference)

### 2.1 선호 설정 저장
```
POST /api/user/preference
```
| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| priceTiers | List\<Enum\> | ✅ | LOW / MID / HIGH (최소 1개) |
| languages | List\<Enum\> | ✅ | KO / EN / JP 등 (최소 1개) |

---

### 2.2 선호 설정 조회
```
GET /api/user/preference
```
**Response**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "priceTiers": ["LOW", "MID"],
    "languages": ["KO", "EN"],
    "hasData": true
  }
}
```

---

## 3. 모임 (Meeting)

### 3.1 모임 리스트 조회
```
GET /api/meetings
```
> 현재 시간 이후 + 정원 미달 모임만 조회  
> ※ 추가정보 입력 필수

**에러 케이스**
| 상황 | 메시지 |
|------|--------|
| 추가정보 미입력 | 모임 서비스를 이용하려면 추가정보를 입력해주세요 |

**Response**
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "slotId": 1,
      "locationArea": "강남",
      "meetDate": "2025-12-01",
      "dayOfWeek": "월",
      "meetTime": "19:00"
    }
  ],
  "totalCount": 1
}
```

---

## 4. 모임 참여 (Entry)

### 4.1 내 모임 조회 (메인 화면)
```
GET /api/meeting/entry/my
```
> ※ 추가정보 입력 필수

**에러 케이스**
| 상황 | 메시지 |
|------|--------|
| 추가정보 미입력 | 모임 서비스를 이용하려면 추가정보를 입력해주세요 |

**Response (D-1 이전 - 정보 미공개)**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "entryId": 1,
    "slotId": 5,
    "locationArea": "강남",
    "meetDate": "2025-12-05",
    "dayOfWeek": "금",
    "meetTime": "19:00",
    "restaurantName": null,
    "restaurantAddr": null,
    "members": [],
    "isInfoRevealed": false,
    "hasActiveMeeting": true
  }
}
```

**Response (D-1 이후 - 정보 공개)**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "entryId": 1,
    "slotId": 5,
    "locationArea": "강남",
    "meetDate": "2025-12-05",
    "dayOfWeek": "금",
    "meetTime": "19:00",
    "restaurantName": "맛있는 식당",
    "restaurantAddr": "서울시 강남구...",
    "members": [
      { "jobCategory": "TECH", "nationality": "KR" },
      { "jobCategory": "ART_CULTURE", "nationality": "US" }
    ],
    "lateCount": 1,
    "isInfoRevealed": true,
    "hasActiveMeeting": true
  }
}
```

**Response (참여 모임 없음)**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "hasActiveMeeting": false
  }
}
```

---

### 4.2 모임 가입
```
POST /api/meeting/entry
```
| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| slotId | Long | ✅ | 모임 ID |

> ※ 추가정보 입력 필수

**에러 케이스**
| 상황 | 메시지 |
|------|--------|
| 추가정보 미입력 | 모임 서비스를 이용하려면 추가정보를 입력해주세요 |
| 모임 없음 | 모임을 찾을 수 없습니다 |
| 시간 지남 | 이미 종료된 모임입니다 |
| OPEN 아님 | 가입할 수 없는 모임 상태입니다 |
| 정원 초과 | 모임 정원이 가득 찼습니다 |
| 중복 가입 | 이미 가입한 모임입니다 |

---

### 4.3 모임 취소
```
DELETE /api/meeting/entry
```
| 필드 | 타입 | 필수 | 설명 |
|------|------|:----:|------|
| cancelReason | String | ✅ | 취소 사유 |

> ※ 추가정보 입력 필수  
> ※ OPEN 상태 모임만 취소 가능 (CONFIRMED 이후 불가)

---

### 4.4 늦어요 알림
```
POST /api/meeting/entry/late
```
> Body 없음  
> ※ 추가정보 입력 필수

**에러 케이스**
| 상황 | 메시지 |
|------|--------|
| 추가정보 미입력 | 모임 서비스를 이용하려면 추가정보를 입력해주세요 |
| 참여 모임 없음 | 참여 중인 모임이 없습니다 |
| 상태 변경 불가 | 늦어요 상태로 변경할 수 없습니다 |

---

### 4.4 늦어요 알림
```
POST /api/meeting/entry/late
```
> Body 없음

- JOINED 상태에서만 가능
- OPEN 또는 CONFIRMED 상태 모임만

---

## 5. 식당 (Restaurant)

### 5.1 식당 전체 조회
```
GET /api/restaurants
```

---

## 6. 공통 코드 (Common)

### 6.1 국적 목록
```
GET /api/common/nationalities
```
> 국적 + 국가코드 (197개국)

**Response**
```json
{
  "code": 0,
  "message": "success",
  "data": [
    { "code": "KR", "name": "대한민국", "dialCode": "+82" },
    { "code": "US", "name": "미국", "dialCode": "+1" }
  ]
}
```

---

### 6.2 성별 목록
```
GET /api/common/genders
```

---

### 6.3 연애 상태 목록
```
GET /api/common/relationship-statuses
```

---

### 6.4 가격대 목록
```
GET /api/common/price-tiers
```

---

### 6.5 직업 카테고리 목록
```
GET /api/common/job-categories
```
**Response**
```json
{
  "code": 0,
  "message": "success",
  "data": [
    { "code": "OFFICE", "name": "관리/사무직" },
    { "code": "TECH", "name": "기술/IT" },
    { "code": "SERVICE", "name": "서비스/판매" },
    { "code": "FOOD", "name": "요식업" },
    { "code": "MEDICAL", "name": "의료/보건" },
    { "code": "EDUCATION", "name": "교육/연구" },
    { "code": "LAW_FINANCE", "name": "법률/금융" },
    { "code": "ART_CULTURE", "name": "예술/문화" },
    { "code": "OTHER", "name": "기타" }
  ]
}
```

---

## 📌 Enum 정리

### Gender (성별)
| 값 | 설명 |
|----|------|
| MALE | 남성 |
| FEMALE | 여성 |

### RelationshipStatus (연애 상태)
| 값 | 설명 |
|----|------|
| SINGLE | 싱글 |
| IN_RELATIONSHIP | 연애중 |
| MARRIED | 기혼 |
| COMPLICATED | 복잡함 |

### PriceTier (선호 가격대)
| 값 | 설명 |
|----|------|
| LOW | 저가 |
| MID | 중가 |
| HIGH | 고가 |

### JobCategory (직업 카테고리)
| 값 | 설명 |
|----|------|
| OFFICE | 관리/사무직 |
| TECH | 기술/IT |
| SERVICE | 서비스/판매 |
| FOOD | 요식업 |
| MEDICAL | 의료/보건 |
| EDUCATION | 교육/연구 |
| LAW_FINANCE | 법률/금융 |
| ART_CULTURE | 예술/문화 |
| OTHER | 기타 |

### MeetingSlotStatus (모임 상태)
| 값 | 설명 |
|----|------|
| OPEN | 모집중 |
| CONFIRMED | 진행중 (모임 시작) |
| CANCELED | 취소됨 |
| FINISHED | 종료됨 |

### SlotEntryStatus (참여 상태)
| 값 | 설명 |
|----|------|
| JOINED | 참여 |
| CANCELED | 취소 |
| LATE | 지각 |
| NOSHOW | 노쇼 |

---

## 📌 공통 응답 형식

**성공 (단건)**
```json
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```

**성공 (리스트)**
```json
{
  "code": 0,
  "message": "success",
  "data": [ ... ],
  "totalCount": 10
}
```

**에러**
```json
{
  "code": 1001,
  "message": "잘못된 파라미터입니다."
}
```

---

## 📌 모임 상태 흐름

```
OPEN (모집중)
    │
    ├─ 모임 시작 시간 + 인원 > 0
    │       ↓
    │   CONFIRMED (진행중)
    │       │
    │       └─ 시작 + 12시간 후 (메인 진입 시)
    │               ↓
    │           FINISHED (종료)
    │
    └─ 인원 = 0 → CANCELED (추후 처리)
```

---

## 📌 정보 공개 조건

| 정보 | 공개 시점 |
|------|---------|
| 기본 정보 (날짜, 시간, 지역) | 항상 |
| 식당 정보 | 모임 D-1부터 |
| 멤버 정보 (직업카테고리/국적) | 모임 D-1부터 |

---

## 📌 API 요약

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | /api/user/basic-info | 기본정보 입력 |
| PATCH | /api/user/profile | 프로필 수정 |
| GET | /api/user/profile | 내 프로필 조회 |
| DELETE | /api/user | 회원 탈퇴 |
| POST | /api/user/logout | 로그아웃 |
| POST | /api/user/preference | 선호 설정 저장 |
| GET | /api/user/preference | 선호 설정 조회 |
| GET | /api/meetings | 모임 리스트 조회 |
| GET | /api/meeting/entry/my | 내 모임 조회 (메인) |
| POST | /api/meeting/entry | 모임 가입 |
| DELETE | /api/meeting/entry | 모임 취소 |
| GET | /api/restaurants | 식당 전체 조회 |
| GET | /api/common/nationalities | 국적 목록 |
| GET | /api/common/genders | 성별 목록 |
| GET | /api/common/relationship-statuses | 연애 상태 목록 |
| GET | /api/common/price-tiers | 가격대 목록 |
| GET | /api/common/job-categories | 직업 카테고리 목록 |
