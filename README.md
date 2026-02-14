# analysis-hangul-jamo

Elasticsearch 9.3.0용 한글 자모 분해 및 초성 추출 analysis 플러그인

## 제공 필터

| Filter | 동작 | 예시 |
|--------|------|------|
| `hangul_jamo` | 한글 음절 → 초+중+종 자모 분해 | 이재국 → ㅇㅣㅈㅐㄱㅜㄱ |
| `hangul_chosung` | 한글 음절 → 초성만 추출 | 이재국 → ㅇㅈㄱ |

## 빌드

```bash
./gradlew bundlePlugin
```

`build/distributions/analysis-hangul-jamo-{version}.zip` 파일이 생성됩니다.

## 설치

[GitHub Releases](https://github.com/irnd04/analysis-hangul-jamo/releases)에서 ZIP 파일을 다운로드하거나, 직접 빌드한 파일을 사용합니다.

```bash
bin/elasticsearch-plugin install file:///path/to/analysis-hangul-jamo-{version}.zip
```

설치 후 Elasticsearch를 재시작해야 합니다.

## Docker로 테스트

```bash
docker compose up -d
```

- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

플러그인 설치 확인:

```bash
curl http://localhost:9200/_cat/plugins
```

자모 분해 테스트:

```bash
curl -s -X POST 'http://localhost:9200/_analyze' \
  -H 'Content-Type: application/json' \
  -d '{"tokenizer":"keyword","filter":["hangul_jamo"],"text":"이재국"}' | jq
```

초성 추출 테스트:

```bash
curl -s -X POST 'http://localhost:9200/_analyze' \
  -H 'Content-Type: application/json' \
  -d '{"tokenizer":"keyword","filter":["hangul_chosung"],"text":"이재국"}' | jq
```

종료:

```bash
docker compose down
```

## Kibana Dev Tools 사용 예시

Kibana (http://localhost:5601) → Management → Dev Tools에서 실행:

### Analyzer 구성

| Analyzer | 설명 | 예시 입력 → 출력                                   |
|----------|------|----------------------------------------------|
| `jamo_index` | 단어별 자모 분해 + edge_ngram (인덱싱용) | "아디다스 운동화" → [ㅇ, ㅇㅏ, ...], [ㅇ, ㅇㅜ, ...]      |
| `jamo_search` | 단어별 자모 분해 (검색용) | "운ㄷ" → "ㅇㅜㄴㄷ"                                |
| `chosung_index` | 단어별 초성 추출 + edge_ngram (인덱싱용) | "아디다스 운동화" → [ㅇ, ㅇㄷ, ㅇㄷㄷ, ...], [ㅇ, ㅇㄷ, ...] |
| `chosung_search` | 단어별 초성 추출 (검색용) | "ㅇㄷㅎ" → "ㅇㄷㅎ"                                |
| `jamo_full_index` | 공백 제거 + 전체 자모 분해 + edge_ngram (인덱싱용) | "아디다스 운동화" → [ㅇ, ㅇㅏ, ..., ㅇㅏㄷㅣㄷㅏㅅㅡㅇㅜㄴㄷㅗㅇㅎㅘ]  |
| `jamo_full_search` | 공백 제거 + 전체 자모 분해 (검색용) | "아디다스운" → "ㅇㅏㄷㅣㄷㅏㅅㅡㅇㅜㄴ"                      |
| `chosung_full_index` | 공백 제거 + 전체 초성 추출 + edge_ngram (인덱싱용) | "아디다스 운동화" → [ㅇ, ㅇㄷ, ..., ㅇㄷㄷㅅㅇㄷㅎ]           |
| `chosung_full_search` | 공백 제거 + 전체 초성 추출 (검색용) | "ㅇㄷㄷㅅ ㅇㄷㅎ" → "ㅇㄷㄷㅅㅇㄷㅎ"                        |

### 인덱스 생성

```json
PUT /korean_search
{
  "settings": {
    "analysis": {
      "filter": {
        "jamo_ngram": { "type": "edge_ngram", "min_gram": 1, "max_gram": 50 },
        "chosung_ngram": { "type": "edge_ngram", "min_gram": 1, "max_gram": 20 },
        "remove_whitespace": { "type": "pattern_replace", "pattern": "\\s+", "replacement": "" }
      },
      "analyzer": {
        "jamo_index": { "tokenizer": "standard", "filter": ["hangul_jamo", "jamo_ngram"] },
        "jamo_search": { "tokenizer": "standard", "filter": ["hangul_jamo"] },
        "chosung_index": { "tokenizer": "standard", "filter": ["hangul_chosung", "chosung_ngram"] },
        "chosung_search": { "tokenizer": "standard", "filter": ["hangul_chosung"] },
        "jamo_full_index": { "tokenizer": "keyword", "filter": ["remove_whitespace", "hangul_jamo", "jamo_ngram"] },
        "jamo_full_search": { "tokenizer": "keyword", "filter": ["remove_whitespace", "hangul_jamo"] },
        "chosung_full_index": { "tokenizer": "keyword", "filter": ["remove_whitespace", "hangul_chosung", "chosung_ngram"] },
        "chosung_full_search": { "tokenizer": "keyword", "filter": ["remove_whitespace", "hangul_chosung"] }
      }
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "fields": {
          "jamo": { "type": "text", "analyzer": "jamo_index", "search_analyzer": "jamo_search" },
          "chosung": { "type": "text", "analyzer": "chosung_index", "search_analyzer": "chosung_search" },
          "jamo_full": { "type": "text", "analyzer": "jamo_full_index", "search_analyzer": "jamo_full_search" },
          "chosung_full": { "type": "text", "analyzer": "chosung_full_index", "search_analyzer": "chosung_full_search" }
        }
      }
    }
  }
}
```

문서 추가:

```json
POST /korean_search/_doc
{"name": "이재국"}

POST /korean_search/_doc
{"name": "아디다스 운동화"}
```

검색:

```json
// 단어 단위 검색 (띄어쓰기 기준)
GET /korean_search/_search
{ "query": { "multi_match": { "query": "ㅇㅈㄱ", "fields": ["name.jamo", "name.chosung"] } } }

GET /korean_search/_search
{ "query": { "multi_match": { "query": "잊", "fields": ["name.jamo^2", "name.chosung"] } } }

GET /korean_search/_search
{ "query": { "multi_match": { "query": "운ㄷ", "fields": ["name.jamo^2", "name.chosung"] } } }

// 전체 문자열 검색 (붙여서 검색)
GET /korean_search/_search
{ "query": { "multi_match": { "query": "ㅇㄷㄷㅅㅇㄷㅎ", "fields": ["name.chosung_full"] } } }

GET /korean_search/_search
{ "query": { "multi_match": { "query": "아디다스운동화", "fields": ["name.jamo_full"] } } }
```
