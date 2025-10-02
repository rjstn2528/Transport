 # TranSport





-----------------------------------------------------------------------------------------------------------------

<br>

## 프로젝트 소개

📌 프로젝트 개요

이 웹사이트는 국내외 여행을 계획하는 사용자들이 비행기, 기차, 버스, 지하철 등 <br> 다양한 교통수단 정보를 한눈에 확인할 수 있도록 돕는 통합 플랫폼입니다. <br>
사용자는 관심 있는 교통편을 선택해 시간표, 요금, 예매 링크 등의 상세 정보를 직관적인 UI를 통해 쉽게 확인할 수 있습니다. <br>
국제선·국내선 항공편, 고속/시외버스, 지하철 노선 등을 카테고리별로 정리하여, <br>
여러 사이트를 번갈아 검색할 필요 없이 하나의 화면에서 필요한 정보를 한 번에 찾을 수 있도록 구성했습니다.



## 주요 기능

* **🖥️ 메인 페이지**    
  웹사이트의 메인 페이지는 사용자들이 국내외 여행에 필요한 다양한 교통수단 정보를 한눈에 확인하고 접근할 수 있도록 구성된 통합 사이트입니다.

  국내여행 통합 검색, 해외 항공 검색, 기차/KTX, 고속·시외버스, 지하철, 항공편 등 <br>
  주요 교통수단을 카테고리별로 구분하여 버튼 형식으로 제공하며, <br>
  각 항목 클릭 시 상세 정보 페이지로 이동됩니다. <br>

  불필요한 탐색 없이 원하는 정보를 빠르게 찾을 수 있도록 <br>
  직관적인 UI/UX와 간편한 화면 구성을 구현하였습니다. <br>

  하단에는 서비스의 핵심 기능 요약 영역을 배치해, <br>
  사이트의 주요 제공 기능과 특징을 한눈에 파악할 수 있습니다. 
  
  
* 🚌 **시외버스 ODsayApiUtil**
사용 용도: 시외버스 정보 제공 <br>
API 출처: ODsay Public Transportation API <br>
주요 기능: 버스터미널 정보, 시외·고속버스 노선 및 운행 시간표 제공
  
* **🚄 기차 TaGoApiUtil**   
  기차역 간 노선, 시간표, 열차 종류(KTX, ITX 등) 스케줄 제공
  
* **✈️국제선, AmadeusApiUtil**   
  국제선 항공 노선, 항공편 스케줄, 공항 정보 등을 API를 통해 조회
  
* **🐍 PythonCrawlerUtil**   
  사용 용도: 일부 공공 API에서 제공하지 않는 데이터 보완용 웹 크롤러


 
<br>

-----------------------------------------------------------------------------------------------------------------

<br>

##  개발 환경   

**언어 / 웹 기술**  
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)  
![JSP](https://img.shields.io/badge/JSP-007396?style=flat&logo=jsp&logoColor=white)  
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)  
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)  
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black)  

**프레임워크 / 라이브러리**  
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white)  

**서버 / DB / 빌드 도구**  
![Apache Tomcat](https://img.shields.io/badge/Apache_Tomcat-F8DC75?style=flat&logo=apachetomcat&logoColor=black)  
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=flat&logo=oracle&logoColor=white)  
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)  

**기타**  
![Python](https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white)


## 🗃 프로젝트 구조
```
📦TranSport
 ┣ 📂src/📂main/📂java📂net📂koreate📂transport
 ┣ 📂config/                      
 ┣ 📂controller/  
 ┣ 📂dao/
 ┣ 📂service/    # 조회 기능
 ┣ 📂util/       # api
 ┣ 📂vo/         # 검색 기능                  
 ┣ 📂src/📂main/📂webapp/               
 ┗ 📂views/       
   ┣ 📂transport/      
   ┣ 📂error       # 에러 이미지
   ┣ 📂bus         # 버스편 조회
   ┣ 📂fight       # 항공편 조회
   ┗ 📂travel      # 기차편 조회

```

---------------------------------------------------------------------------------------------------



-----------------------------------------------------------------------------------------------------------------
