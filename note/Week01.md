## 1. 강의 소개

### 1-1 Course 소개

+ 데이터 분석의 중요성
  + 인터넷과 스마트폰의 발전에 따라 데이터가 증가함에 따라 데이터는 점점 중요해지고 있음
  + 새로운 시각을 가지기 위해 데이터를 다양하게 분석해야 하는 중요성이 커짐
    + 새로운 통찰력과 결론을 얻기 위해 수많은 데이터에서 새로운 패턴과 상관 관계를 발견해야 한다.


+ 비정형 데이터의 특징

  + 규모	

    + 페이스북 : 1달 간 300억 글이 공유
    + 트위터 : 1일 간 4억 트윗 전송

  + 다양성

    데이터셋의 구조적인 이절성을 나타냄

    + 구조화 데이터 : 모든 데이트의 5%
    + 준구조화 데이터 : XML
    + 비구조화 데이터 : 텍스트, 이미지, 오디오 등

  + 변동성

    데이터는 다양한 요소에서 변화할 수 있음

    + 데이터의 사용 권한
    + 시간에 따른 데이터의 변화 여부

  + 속도

    + 데이터가 생성되고 분석되는 속도


    + 스마트 기기의 발전으로 데이터를 생성하는 속도가 매우 증가함

  + 정확성

    부정확하고 불확실한 일부 데이터

    + 소셜 미디어 이용자들의 감정을 정확히 알 수 없음

  + 의미(Semantics)

    + 기존 데이터에서는 데이터의 의미와 데이터 간의 관계를 이해하기 힘듦
    + 사람이 직접 해석하지 않고 기계가 데이터를 의미있게 해석 할 수 있게끔 함

  + 복잡성

    + 데이터는 구조, 형식, 내용 등에 따라 다르게 나타남
    + 서로 다른 데이터들을 연결하여 새로운 데이터를 생성할 수 있음

+ 텍스트 마이닝이란

  + 대량의 텍스트 데이터셋에서 흥미로운 규칙들을 찾아내는 것
    + 흥미로운 규칙 : 이전에는 알려지지 않고, 잠재적이고, 감추어진 규칙
  + 문자로 된 다른 자료들로부터 자동적으로 정보를 추출하는, 이전에 알려지지 않은 새로운 정보의 발견


### 1.2 yTextMiner 소개

+ 소개
  + 연세대학교에서 제작한 Java패키지
  + 빠르고 쉽게 텍스트를 분석할 수 있음
  + Stanford CoreNLP구조화 유사하며. Pipeline을 기반으로 함
    + 기존 모델을 개량하여 성능을 향상시킴
      + 토픽 모델링
      + 문헌 범주화
  + 한글 텍스트와 영어 텍스트를 처리할 수 있도록 고안됨
    + 한글 라이브러리 : Komoran(보고서, 논문등 정형화 된 글), Twitter Korean
    + 영어 라이브러리 : Stanford CoreNLP' 
  + 기타 라이브러리
    + U Mass Arherst Mallet : 토픽 모델링
    + Alias-i Lingpipe : 문헌범주화, 감성분석, 개체 메일 인식
    + NTU LibLinear : 문헌을 자동 범주화, 기계학습
    + ISTI SentiWordNet : 감성분석(영어), 사전기반으로 하는 사전

+ 구조

  + Token : 가장 하위 구조(단어), 단어의 품사 원형 개체명 불용어 정보

  + Sentence : Token의 합 (문장)

    + 문장과 문장의 감성 점수를 포함함

  + Document : Sentence의 합(문헌)

    + 문서 분류

      + Naive Bayes
      + Logistic Regression
      + LibLinear의 LinearSVM

    + 감성 분석

      Lingpipe의 Sentiment Analysis

  + Collection : Document의 합

    + 문서 집합과 이를 이용한 모델
      + Mallet의  LDA

+ 특징

  + 전처리

    Stanford CoreNLP의 전처리 pipe

  + 감성분석

    Stanford CoreNLP의 감성 분석 도구

    + 비지도 학습 : SentiWordNet
    + 지도 학습 : Stanford CoreNLP

  + 토픽 모델링

    + LDA : 단어 군집
    + DMR : 단어를 시계열적으로 분석

  + 문헌 범주화(Document Classification)

    대부분 지도 학습으로 진행

+ 데이터셋

  세가지 데이터셋을 이용

  + Github : developer.github.com/v3/
  + New York Times :  developer.nytimes.com
  + Health Data : www.healthdata.gov/api
