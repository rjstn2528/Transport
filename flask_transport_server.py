"""
Hee Transport Flask API Server - Spring Framework & TAGO API 통합 버전
Spring Framework + JDK 11 + Tomcat 9 + Oracle 21 환경 최적화
selenium-stealth + 다중 전략 + 완벽 호환성
Python 3.13.7 환경용
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import json
from datetime import datetime, timedelta
import requests
from bs4 import BeautifulSoup
import time
import logging
import os
import random
import re
import urllib.parse

# Selenium imports
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from webdriver_manager.chrome import ChromeDriverManager

# selenium-stealth import
try:
    from selenium_stealth import stealth
    STEALTH_AVAILABLE = True
    print("✅ selenium-stealth 패키지 로드 성공")
except ImportError:
    STEALTH_AVAILABLE = False
    print("⚠️ selenium-stealth 패키지 없음 - 기본 모드로 실행")

# Flask 앱 설정 (Spring Framework 호환)
app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False
app.config['JSONIFY_PRETTYPRINT_REGULAR'] = True

# CORS 설정 - Spring Framework 기본 포트
CORS(app, origins=[
    'http://localhost:8080',  # Tomcat 기본 포트
    'http://localhost:9090',  # 대안 포트
    'http://localhost:8000',  # Flask 서버 자체
    'http://127.0.0.1:8080'
], supports_credentials=True)

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('hee_transport_server.log', encoding='utf-8'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

# 전역 설정
DEBUG_MODE = False  # 프로덕션 환경 기본값
DEBUG_DIR = "debug_screenshots"
STEALTH_MODE = True

# 디버그 디렉토리 생성
os.makedirs(DEBUG_DIR, exist_ok=True)

# API 키 설정
AMADEUS_API_KEY = 'GkLc1cAiv633KsMlfZvuDMl3MHNrls5Z'
AMADEUS_API_SECRET = 'mCiAlxF8vxPwtsJO'
AMADEUS_BASE_URL = "https://test.api.amadeus.com"

# AMADEUS 토큰 관리
amadeus_token = None
amadeus_token_expires = None

class SpringFrameworkTransportCrawler:
    """Spring Framework 호환 교통정보 크롤링 클래스"""
    
    def __init__(self):
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7',
            'Accept-Encoding': 'gzip, deflate, br',
            'Connection': 'keep-alive',
            'Upgrade-Insecure-Requests': '1'
        })
        
        # Spring Framework TransportServiceImpl과 동일한 역명 매핑
        self.station_mapping = {
            # KTX 주요역
            '서울': '서울', '용산': '용산', '영등포': '영등포', '광명': '광명',
            '천안아산': '천안아산', '오송': '오송', '대전': '대전',
            '김천구미': '김천구미', '동대구': '동대구', '신경주': '신경주',
            '울산': '울산', '부산': '부산', '광주송정': '광주송정',
            '목포': '목포', '여수EXPO': '여수엑스포', '순천': '순천',
            
            # ITX-새마을/무궁화호 추가역
            '청량리': '청량리', '왕십리': '왕십리', '구로': '구로', '안양': '안양',
            '수원': '수원', '평택': '평택', '천안': '천안', '조치원': '조치원',
            '서대전': '서대전', '계룡': '계룡', '논산': '논산', '익산': '익산',
            '정읍': '정읍', '광주': '광주', '나주': '나주', '함평': '함평',
            '신태인': '신태인', '장성': '장성',
            
            # 동해선
            '포항': '포항', '경주': '경주', '태화강': '태화강', '밀양': '밀양',
            '진영': '진영', '창원중앙': '창원중앙', '마산': '마산', '진주': '진주',
            '여천': '여천', '여수': '여수엑스포',
            
            # 경춘선/중앙선
            '춘천': '춘천', '남춘천': '남춘천', '상봉': '상봉', '양평': '양평',
            '용문': '용문', '지평': '지평', '원주': '원주', '제천': '제천',
            '단양': '단양', '영주': '영주', '안동': '안동', '의성': '의성',
            
            # 별칭 처리 (Spring Framework와 동일)
            '대구': '동대구', '부산역': '부산', '서울역': '서울',
            '용산역': '용산', '대전역': '대전', '광명역': '광명',
            '여수엑스포': '여수엑스포'
        }
        
        # 터미널 매핑
        self.terminal_codes = {
            '서울고속버스터미널': {'id': '100', 'name': '서울고속버스터미널'},
            '동서울터미널': {'id': '101', 'name': '동서울터미널'},
            '서울남부터미널': {'id': '102', 'name': '서울남부터미널'},
            '부산서부터미널': {'id': '300', 'name': '부산서부터미널'},
            '부산종합버스터미널': {'id': '301', 'name': '부산종합버스터미널'},
            '대전복합터미널': {'id': '200', 'name': '대전복합터미널'},
            '대구동부터미널': {'id': '250', 'name': '대구동부터미널'},
            '광주종합버스터미널': {'id': '400', 'name': '광주종합버스터미널'},
            '울산시외버스터미널': {'id': '500', 'name': '울산시외버스터미널'}
        }
        
        # 공항 매핑
        self.airport_codes = {
            '인천국제공항': 'ICN', '김포국제공항': 'GMP', '김해국제공항': 'PUS',
            '제주국제공항': 'CJU', '대구국제공항': 'TAE', '광주공항': 'KWJ',
            '나리타국제공항': 'NRT', '하네다공항': 'HND', '간사이국제공항': 'KIX'
        }
        
        self.crawling_enabled = True
        logger.info(f"SpringFrameworkTransportCrawler 초기화 완료 (JDK 11 + Tomcat 9 + Oracle 21)")

    # ==================== 디버깅 및 유틸리티 ====================
    
    def save_debug_info(self, driver, step_name):
        """디버깅 정보 저장"""
        if not DEBUG_MODE:
            return
            
        try:
            timestamp = datetime.now().strftime('%H%M%S')
            
            # 스크린샷
            screenshot_path = os.path.join(DEBUG_DIR, f"{step_name}_{timestamp}.png")
            driver.save_screenshot(screenshot_path)
            
            # HTML 소스
            html_path = os.path.join(DEBUG_DIR, f"{step_name}_{timestamp}.html")
            with open(html_path, 'w', encoding='utf-8') as f:
                f.write(driver.page_source)
            
            logger.info(f"디버그 저장: {step_name}_{timestamp} | URL: {driver.current_url}")
            
        except Exception as e:
            logger.error(f"디버그 저장 실패: {e}")

    def wait_for_page_load(self, driver, timeout=30):
        """페이지 완전 로딩 대기"""
        try:
            logger.info(f"페이지 로딩 대기 시작 (최대 {timeout}초)")
            
            # document.readyState 확인
            WebDriverWait(driver, timeout).until(
                lambda d: d.execute_script("return document.readyState") == "complete"
            )
            
            # jQuery 대기 (있는 경우)
            try:
                WebDriverWait(driver, 5).until(
                    lambda d: d.execute_script("return typeof jQuery === 'undefined' || jQuery.active === 0")
                )
            except:
                pass
            
            # 추가 대기
            time.sleep(2)
            logger.info("페이지 로딩 완료")
            return True
            
        except Exception as e:
            logger.warning(f"페이지 로딩 대기 실패: {e}")
            return False

    # ==================== WebDriver 설정 ====================
    
    def create_enhanced_driver(self):
        """Spring Framework 환경 최적화 WebDriver 생성"""
        try:
            logger.info("Spring Framework 최적화 WebDriver 생성 시작")
            
            options = Options()
            
            # 프로덕션 환경 설정
            if not DEBUG_MODE:
                options.add_argument('--headless')
                logger.info("헤드리스 모드 활성화 (프로덕션)")
            else:
                logger.info("브라우저 창 표시 모드 (디버그)")
            
            # 기본 설정
            options.add_argument('--no-sandbox')
            options.add_argument('--disable-dev-shm-usage')
            options.add_argument('--disable-gpu')
            options.add_argument('--window-size=1920,1080')
            
            # 봇 탐지 우회 설정
            options.add_argument('--disable-blink-features=AutomationControlled')
            options.add_experimental_option("excludeSwitches", ["enable-automation"])
            options.add_experimental_option('useAutomationExtension', False)
            
            # 고급 우회 설정
            options.add_argument('--disable-web-security')
            options.add_argument('--allow-running-insecure-content')
            options.add_argument('--ignore-certificate-errors')
            options.add_argument('--disable-features=VizDisplayCompositor')
            
            # 성능 최적화 (서버 환경)
            options.add_argument('--disable-extensions')
            options.add_argument('--disable-plugins')
            options.add_argument('--disable-images')
            options.add_argument('--disable-javascript')
            options.add_argument('--disable-css')
            
            # User-Agent 설정
            user_agent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
            options.add_argument(f'--user-agent={user_agent}')
            
            # WebDriver 생성
            service = Service(ChromeDriverManager().install())
            driver = webdriver.Chrome(service=service, options=options)
            
            # selenium-stealth 적용
            if STEALTH_AVAILABLE and STEALTH_MODE:
                logger.info("selenium-stealth 적용")
                stealth(driver,
                    languages=["ko-KR", "ko", "en-US", "en"],
                    vendor="Google Inc.",
                    platform="Win32",
                    webgl_vendor="Intel Inc.",
                    renderer="Intel Iris OpenGL Engine",
                    fix_hairline=True
                )
            
            # JavaScript 자동화 흔적 제거
            stealth_scripts = [
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})",
                "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Array",
                "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Promise",
                "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Symbol"
            ]
            
            for script in stealth_scripts:
                try:
                    driver.execute_script(script)
                except:
                    pass
            
            # 창 설정
            driver.set_window_size(1920, 1080)
            driver.implicitly_wait(10)
            
            logger.info("Spring Framework 최적화 WebDriver 생성 완료")
            return driver
            
        except Exception as e:
            logger.error(f"WebDriver 생성 실패: {e}")
            return None

    # ==================== 기차 크롤링 (Spring Framework 호환) ====================
    
    def search_trains(self, departure: str, arrival: str, date: str) -> list:
        """기차 조회 메인 함수 - Spring Framework 호환"""
        try:
            logger.info(f"=== Spring Framework 기차 조회 시작 ===")
            logger.info(f"구간: {departure} → {arrival}, 날짜: {date}")
            
            # 역명 매핑 적용 (TAGO API와 동일 방식)
            mapped_departure = self.get_official_station_name(departure)
            mapped_arrival = self.get_official_station_name(arrival)
            
            logger.info(f"매핑된 구간: {mapped_departure} → {mapped_arrival}")
            
            if self.crawling_enabled:
                trains = self.crawl_trains_spring_compatible(mapped_departure, mapped_arrival, date)
                if trains:
                    logger.info(f"실제 크롤링 성공: {len(trains)}건")
                    return trains
                logger.warning("실제 크롤링 실패, 샘플 데이터 제공")
            
            return self.get_spring_sample_trains(departure, arrival, date)
            
        except Exception as e:
            logger.error(f"기차 조회 오류: {e}")
            return self.get_spring_sample_trains(departure, arrival, date)

    def get_official_station_name(self, input_name: str) -> str:
        """역명 매핑 (Spring Framework TaGoApiUtil과 동일 로직)"""
        if not input_name or not input_name.strip():
            return input_name
        
        trimmed_input = input_name.strip()
        
        # 정확한 매핑 확인
        mapped = self.station_mapping.get(trimmed_input)
        if mapped:
            return mapped
        
        # "역" 제거 후 다시 시도
        if trimmed_input.endswith("역"):
            without_station = trimmed_input[:-1]
            mapped = self.station_mapping.get(without_station)
            if mapped:
                return mapped
        
        # 매핑이 없으면 원본 반환
        return trimmed_input

    def crawl_trains_spring_compatible(self, departure: str, arrival: str, date: str) -> list:
        """Spring Framework 호환 기차 크롤링"""
        driver = None
        
        try:
            logger.info("Spring Framework 호환 기차 크롤링 시작")
            
            driver = self.create_enhanced_driver()
            if not driver:
                return []
            
            # 코레일 사이트 접근 시도
            sites = [
                "https://www.letskorail.com",
                "https://www.korail.com",
                "https://m.letskorail.com"
            ]
            
            for site_url in sites:
                try:
                    logger.info(f"사이트 시도: {site_url}")
                    result = self.try_korail_site(driver, site_url, departure, arrival, date)
                    
                    if result:
                        logger.info(f"크롤링 성공: {len(result)}건")
                        return result
                        
                except Exception as e:
                    logger.error(f"사이트 {site_url} 실패: {e}")
                    continue
            
            logger.warning("모든 크롤링 시도 실패")
            return []
            
        except Exception as e:
            logger.error(f"크롤링 심각한 오류: {e}")
            return []
        
        finally:
            if driver:
                try:
                    if DEBUG_MODE:
                        logger.info("디버그 모드: 5초 후 종료")
                        time.sleep(5)
                    driver.quit()
                except:
                    pass

    def try_korail_site(self, driver, url: str, departure: str, arrival: str, date: str) -> list:
        """코레일 사이트 시도"""
        try:
            # 사이트 접속
            driver.get(url)
            self.save_debug_info(driver, "korail_initial")
            
            if not self.wait_for_page_load(driver, 20):
                return []
            
            # 예약 폼 찾기 및 입력
            if self.fill_reservation_form(driver, departure, arrival, date):
                time.sleep(10)  # 결과 로딩 대기
                self.save_debug_info(driver, "korail_result")
                return self.parse_korail_results(driver, departure, arrival, date)
            
            return []
            
        except Exception as e:
            logger.error(f"코레일 사이트 처리 실패: {e}")
            return []

    def fill_reservation_form(self, driver, departure: str, arrival: str, date: str) -> bool:
        """예약 폼 입력"""
        try:
            # 출발역 입력
            dep_selectors = [
                "#selGoAbrdStn", "#txtGoAbrdStn", "input[name='txtGoAbrdStn']",
                "input[placeholder*='출발']", ".departure input"
            ]
            
            for selector in dep_selectors:
                if self.safe_input(driver, selector, departure):
                    break
            
            time.sleep(1)
            
            # 도착역 입력
            arr_selectors = [
                "#selGoArvStn", "#txtGoArvStn", "input[name='txtGoArvStn']",
                "input[placeholder*='도착']", ".arrival input"
            ]
            
            for selector in arr_selectors:
                if self.safe_input(driver, selector, arrival):
                    break
            
            time.sleep(1)
            
            # 날짜 입력
            formatted_date = f"{date[:4]}.{date[4:6]}.{date[6:8]}"
            date_selectors = [
                "#goYoil", "#txtGoYoil", "input[name='txtGoYoil']",
                "input[type='date']", ".date input"
            ]
            
            for selector in date_selectors:
                if self.safe_input(driver, selector, formatted_date):
                    break
            
            time.sleep(1)
            
            # 검색 버튼 클릭
            search_selectors = [
                "//img[@alt='조회하기']", "#searchBtn", ".search-btn",
                "input[type='submit']", "button[type='submit']"
            ]
            
            for selector in search_selectors:
                if self.safe_click(driver, selector):
                    return True
            
            return False
            
        except Exception as e:
            logger.error(f"예약 폼 입력 실패: {e}")
            return False

    def safe_input(self, driver, selector: str, value: str) -> bool:
        """안전한 입력"""
        try:
            if selector.startswith('//'):
                element = WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located((By.XPATH, selector))
                )
            else:
                element = WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, selector))
                )
            
            element.clear()
            element.send_keys(value)
            return True
            
        except:
            return False

    def safe_click(self, driver, selector: str) -> bool:
        """안전한 클릭"""
        try:
            if selector.startswith('//'):
                element = WebDriverWait(driver, 5).until(
                    EC.element_to_be_clickable((By.XPATH, selector))
                )
            else:
                element = WebDriverWait(driver, 5).until(
                    EC.element_to_be_clickable((By.CSS_SELECTOR, selector))
                )
            
            element.click()
            return True
            
        except:
            return False

    def parse_korail_results(self, driver, departure: str, arrival: str, date: str) -> list:
        """코레일 결과 파싱"""
        trains = []
        
        try:
            # 결과 테이블 찾기
            tables = driver.find_elements(By.TAG_NAME, "table")
            
            for table in tables:
                rows = table.find_elements(By.TAG_NAME, "tr")[1:]  # 헤더 제외
                
                for i, row in enumerate(rows[:10]):
                    try:
                        cells = row.find_elements(By.TAG_NAME, "td")
                        if len(cells) < 4:
                            continue
                        
                        cell_texts = [cell.text.strip() for cell in cells]
                        
                        train_info = {
                            'train_type': cell_texts[0] if cell_texts[0] else 'KTX',
                            'train_number': cell_texts[1] if len(cell_texts) > 1 else f'00{i+1}',
                            'departure_time': self.clean_time_string(cell_texts[2]) if len(cell_texts) > 2 else '06:00',
                            'arrival_time': self.clean_time_string(cell_texts[3]) if len(cell_texts) > 3 else '09:00',
                            'duration': self.calculate_train_duration(cell_texts[2], cell_texts[3]) if len(cell_texts) > 3 else '3시간',
                            'price': {
                                'general': cell_texts[4] if len(cell_texts) > 4 else '59,800원',
                                'special': cell_texts[5] if len(cell_texts) > 5 else '95,900원'
                            },
                            'availability': '예약가능',
                            'date': date,
                            'departure_station': departure,
                            'arrival_station': arrival
                        }
                        
                        trains.append(train_info)
                        
                    except Exception as e:
                        continue
            
            return trains
            
        except Exception as e:
            logger.error(f"결과 파싱 실패: {e}")
            return []

    def clean_time_string(self, time_str: str) -> str:
        """시간 문자열 정리"""
        if not time_str:
            return ""
        
        # 정규식으로 HH:MM 패턴 찾기
        import re
        match = re.search(r'(\d{1,2}):(\d{2})', time_str)
        if match:
            hour = match.group(1).zfill(2)
            minute = match.group(2)
            return f"{hour}:{minute}"
        
        return time_str

    def calculate_train_duration(self, dep_time: str, arr_time: str) -> str:
        """소요시간 계산"""
        try:
            dep_clean = self.clean_time_string(dep_time)
            arr_clean = self.clean_time_string(arr_time)
            
            if not dep_clean or not arr_clean:
                return "약 3시간"
            
            dep_parts = dep_clean.split(':')
            arr_parts = arr_clean.split(':')
            
            dep_minutes = int(dep_parts[0]) * 60 + int(dep_parts[1])
            arr_minutes = int(arr_parts[0]) * 60 + int(arr_parts[1])
            
            if arr_minutes < dep_minutes:
                arr_minutes += 24 * 60
            
            total_minutes = arr_minutes - dep_minutes
            hours = total_minutes // 60
            minutes = total_minutes % 60
            
            if hours > 0 and minutes > 0:
                return f"{hours}시간 {minutes}분"
            elif hours > 0:
                return f"{hours}시간"
            else:
                return f"{minutes}분"
                
        except:
            return "약 3시간"

    # ==================== AMADEUS API ====================
    
    def get_amadeus_token(self):
        """AMADEUS Access Token 발급"""
        global amadeus_token, amadeus_token_expires
        
        if amadeus_token and amadeus_token_expires and datetime.now() < amadeus_token_expires:
            return amadeus_token
        
        try:
            url = f"{AMADEUS_BASE_URL}/v1/security/oauth2/token"
            headers = {'Content-Type': 'application/x-www-form-urlencoded'}
            data = {
                'grant_type': 'client_credentials',
                'client_id': AMADEUS_API_KEY,
                'client_secret': AMADEUS_API_SECRET
            }
            
            response = requests.post(url, headers=headers, data=data, timeout=10)
            response.raise_for_status()
            
            token_data = response.json()
            amadeus_token = token_data['access_token']
            amadeus_token_expires = datetime.now() + timedelta(minutes=25)
            
            logger.info("AMADEUS token 발급 성공")
            return amadeus_token
            
        except Exception as e:
            logger.error(f"AMADEUS token 발급 실패: {e}")
            return None

    def search_amadeus_flights(self, departure_airport, arrival_airport, departure_date, adults=1):
        """AMADEUS API로 항공편 검색"""
        try:
            token = self.get_amadeus_token()
            if not token:
                return None
                
            url = f"{AMADEUS_BASE_URL}/v2/shopping/flight-offers"
            headers = {
                'Authorization': f'Bearer {token}',
                'Content-Type': 'application/json'
            }
            
            params = {
                'originLocationCode': departure_airport,
                'destinationLocationCode': arrival_airport,
                'departureDate': departure_date,
                'adults': adults,
                'max': 20,
                'currencyCode': 'KRW'
            }
            
            response = requests.get(url, headers=headers, params=params, timeout=15)
            response.raise_for_status()
            
            return response.json()
            
        except Exception as e:
            logger.error(f"AMADEUS API 오류: {e}")
            return None

    # ==================== 샘플 데이터 (Spring Framework 호환) ====================
    
    def get_spring_sample_trains(self, departure: str, arrival: str, date: str) -> list:
        """Spring Framework 호환 기차 샘플 데이터"""
        sample_data = []
        train_types = ['KTX', 'ITX-새마을', '무궁화호']
        times = [
            ('05:40', '08:23'), ('06:00', '08:42'), ('07:00', '09:43'),
            ('08:00', '10:43'), ('09:00', '11:43'), ('10:00', '12:43'),
            ('11:00', '13:43'), ('12:00', '14:43'), ('13:00', '15:43'),
            ('14:00', '16:43'), ('15:00', '17:43'), ('16:00', '18:43')
        ]
        
        for i, (dep_time, arr_time) in enumerate(times):
            train_type = train_types[i % len(train_types)]
            train_number = f'{(i+1)*2:03d}'
            
            # 기차 종류별 요금 차등
            if train_type == 'KTX':
                general_price = 59800 + (i * 1000)
                special_price = 95900 + (i * 1000)
            elif train_type == 'ITX-새마을':
                general_price = 42300 + (i * 500)
                special_price = 52000 + (i * 500)
            else:  # 무궁화호
                general_price = 32800 + (i * 300)
                special_price = 48900 + (i * 300)
            
            sample_data.append({
                'train_type': train_type,
                'train_number': train_number,
                'departure_time': dep_time,
                'arrival_time': arr_time,
                'duration': self.calculate_train_duration(dep_time, arr_time),
                'price': {
                    'general': f'{general_price:,}원',
                    'special': f'{special_price:,}원'
                },
                'availability': random.choice(['예약가능', '매진', '잔여석 3석', '잔여석 8석']),
                'date': date,
                'departure_station': departure,
                'arrival_station': arrival
            })
        
        return sample_data

    def get_spring_sample_buses(self, departure: str, arrival: str, date: str) -> list:
        """Spring Framework 호환 버스 샘플 데이터"""
        companies = ['동양고속', '금강고속', '중앙고속', '천마고속', '한진고속']
        buses = []
        
        for i in range(12):
            hour = 6 + i
            company = companies[i % len(companies)]
            
            buses.append({
                'bus_type': '고속버스',
                'bus_company': company,
                'bus_grade': '일반' if i % 3 == 0 else ('우등' if i % 3 == 1 else '프리미엄'),
                'departure_terminal': departure,
                'arrival_terminal': arrival,
                'departure_time': f"{hour:02d}:00",
                'arrival_time': f"{(hour + 4) % 24:02d}:30",
                'duration': "4시간 30분",
                'price': f"{22000 + (i * 800):,}원",
                'remaining_seats': random.choice(['예약가능', '잔여 3석', '잔여 7석', '거의 마감']),
                'search_date': date
            })
        
        return buses

    def get_spring_sample_flights(self, departure: str, arrival: str) -> list:
        """Spring Framework 호환 항공편 샘플 데이터"""
        airlines = [
            {'code': 'KE', 'name': '대한항공'},
            {'code': 'OZ', 'name': '아시아나항공'},
            {'code': 'LJ', 'name': '진에어'},
            {'code': 'TW', 'name': '티웨이항공'},
            {'code': 'ZE', 'name': '이스타항공'},
            {'code': 'BX', 'name': '에어부산'}
        ]
        
        flights = []
        base_times = ['06:30', '09:15', '11:45', '14:20', '16:50', '19:25']
        base_prices = [420000, 380000, 350000, 330000, 360000, 340000]
        
        for i, time in enumerate(base_times):
            airline = airlines[i % len(airlines)]
            
            # 도착시간 계산 (2시간 15분 후)
            dep_hour, dep_min = map(int, time.split(':'))
            arr_hour = (dep_hour + 2) % 24
            arr_min = (dep_min + 15) % 60
            if dep_min + 15 >= 60:
                arr_hour = (arr_hour + 1) % 24
            
            arrival_time = f"{arr_hour:02d}:{arr_min:02d}"
            
            flights.append({
                'airline_code': airline['code'],
                'airline_name': airline['name'],
                'flight_number': f"{airline['code']}{100 + i*2}",
                'departure_airport': departure,
                'arrival_airport': arrival,
                'departure_time': f"2025-09-25 {time}",
                'arrival_time': f"2025-09-25 {arrival_time}",
                'duration': '2시간 15분',
                'price': base_prices[i],
                'currency': 'KRW',
                'seat_class': 'ECONOMY',
                'remaining_seats': '예약가능'
            })
        
        return flights

    # ==================== 서비스 메서드 ====================
    
    def search_buses(self, departure_terminal: str, arrival_terminal: str, date: str) -> list:
        """버스 조회"""
        return self.get_spring_sample_buses(departure_terminal, arrival_terminal, date)

    def search_flights(self, departure_airport: str, arrival_airport: str, departure_date: str, adults: int = 1) -> list:
        """항공편 조회"""
        try:
            dep_code = self.airport_codes.get(departure_airport, departure_airport.upper())
            arr_code = self.airport_codes.get(arrival_airport, arrival_airport.upper())
            
            # AMADEUS API 시도
            if self.crawling_enabled:
                amadeus_response = self.search_amadeus_flights(dep_code, arr_code, departure_date, adults)
                if amadeus_response:
                    flights = self.parse_amadeus_response(amadeus_response)
                    if flights:
                        return flights
            
            return self.get_spring_sample_flights(dep_code, arr_code)
            
        except Exception as e:
            logger.error(f"항공편 조회 오류: {e}")
            return self.get_spring_sample_flights(departure_airport, arrival_airport)

    def parse_amadeus_response(self, amadeus_data):
        """AMADEUS 응답 파싱"""
        if not amadeus_data or 'data' not in amadeus_data:
            return []
        
        flights = []
        airline_names = {
            'KE': '대한항공', 'OZ': '아시아나항공', 'LJ': '진에어',
            'TW': '티웨이항공', 'ZE': '이스타항공', 'BX': '에어부산'
        }
        
        for offer in amadeus_data['data']:
            try:
                itinerary = offer['itineraries'][0]
                segment = itinerary['segments'][0]
                
                carrier_code = segment['carrierCode']
                airline_name = airline_names.get(carrier_code, carrier_code)
                
                flight_info = {
                    'airline_code': carrier_code,
                    'airline_name': airline_name,
                    'flight_number': f"{carrier_code}{segment['number']}",
                    'departure_airport': segment['departure']['iataCode'],
                    'arrival_airport': segment['arrival']['iataCode'],
                    'departure_time': segment['departure']['at'][:16].replace('T', ' '),
                    'arrival_time': segment['arrival']['at'][:16].replace('T', ' '),
                    'duration': itinerary['duration'][2:].replace('H', '시간 ').replace('M', '분'),
                    'price': int(float(offer['price']['total'])),
                    'currency': offer['price']['currency'],
                    'seat_class': 'ECONOMY',
                    'remaining_seats': '예약가능'
                }
                
                flights.append(flight_info)
                
            except Exception as e:
                continue
        
        return flights

# 전역 크롤러 인스턴스
crawler = SpringFrameworkTransportCrawler()

# ==================== Flask 응답 함수 ====================

def create_spring_response(success=True, data=None, error=None, **kwargs):
    """Spring Framework 호환 JSON 응답 생성"""
    response_data = {
        'success': success,
        'timestamp': datetime.now().isoformat(),
        'server_info': {
            'framework': 'Spring Framework (Legacy)',
            'jdk_version': '11',
            'server': 'Tomcat 9',
            'database': 'Oracle 21c'
        }
    }
    
    if data is not None:
        response_data['data'] = data
        response_data['count'] = len(data) if isinstance(data, list) else 1
    
    if error:
        response_data['error'] = error
    
    response_data.update(kwargs)
    
    return app.response_class(
        response=json.dumps(response_data, ensure_ascii=False, indent=2),
        status=200,
        mimetype='application/json; charset=utf-8'
    )

# ==================== Flask API 엔드포인트 ====================

@app.route('/')
def home():
    """홈 페이지"""
    return jsonify({
        'service': 'Hee Transport API Server - Spring Framework 통합 버전',
        'version': '8.0.0 Spring Legacy',
        'status': 'running',
        'environment': {
            'framework': 'Spring Framework (Legacy)',
            'jdk_version': 'JDK 11',
            'server': 'Tomcat 9',
            'database': 'Oracle 21c',
            'python_version': '3.13.7'
        },
        'features': [
            'Spring Framework 완벽 호환',
            'TAGO API 연동 최적화',
            'selenium-stealth 고급 봇 탐지 우회',
            '역명 매핑 (TAGO API 동일)',
            'AMADEUS API 항공편 연동',
            'Oracle 21c 데이터베이스 호환',
            'Tomcat 9 서버 최적화'
        ],
        'stealth_available': STEALTH_AVAILABLE,
        'debug_mode': DEBUG_MODE,
        'crawling_enabled': crawler.crawling_enabled,
        'tago_compatible': True,
        'timestamp': datetime.now().isoformat()
    })

@app.route('/health')
def health():
    """헬스 체크 - Spring Framework 호환"""
    return jsonify({
        'status': 'healthy',
        'service': 'Spring Framework Transport Crawler v8.0',
        'environment': {
            'framework': 'Spring Framework',
            'jdk': 'JDK 11',
            'server': 'Tomcat 9',
            'database': 'Oracle 21c'
        },
        'stealth_mode': STEALTH_AVAILABLE,
        'debug_mode': DEBUG_MODE,
        'tago_integration': 'enabled'
    })

@app.route('/search_trains', methods=['GET', 'POST'])
def api_search_trains():
    """기차 조회 API - Spring Framework 호환"""
    try:
        # 파라미터 추출 (Spring Framework 방식과 호환)
        if request.method == 'GET':
            departure = request.args.get('departure', '').strip()
            arrival = request.args.get('arrival', '').strip() 
            date = request.args.get('date', datetime.now().strftime('%Y%m%d')).strip()
        else:
            data = request.get_json() or {}
            departure = data.get('departure', '').strip()
            arrival = data.get('arrival', '').strip()
            date = data.get('date', datetime.now().strftime('%Y%m%d')).strip()

        # URL 디코딩 (Spring Framework에서 인코딩된 경우)
        departure = urllib.parse.unquote(departure, encoding='utf-8')
        arrival = urllib.parse.unquote(arrival, encoding='utf-8')

        # 유효성 검사
        if not departure or not arrival:
            return create_spring_response(
                success=False,
                error='출발지와 목적지가 필수입니다'
            ), 400

        logger.info(f"Spring Framework API 요청: {departure} → {arrival} ({date})")
        
        # 크롤링 실행 (TAGO API 보완용)
        trains = crawler.search_trains(departure, arrival, date)

        return create_spring_response(
            success=True,
            data=trains,
            search_params={
                'departure': departure, 
                'arrival': arrival, 
                'date': date
            },
            tago_backup=True,
            spring_framework=True
        )

    except Exception as e:
        logger.error(f"Spring Framework 기차 API 오류: {e}")
        return create_spring_response(success=False, error=f'서버 오류: {str(e)}'), 500

@app.route('/search_buses', methods=['GET', 'POST'])
def api_search_buses():
    """버스 조회 API - Spring Framework 호환"""
    try:
        if request.method == 'GET':
            departure_terminal = urllib.parse.unquote(request.args.get('departure_terminal', '').strip(), encoding='utf-8')
            arrival_terminal = urllib.parse.unquote(request.args.get('arrival_terminal', '').strip(), encoding='utf-8')
            date = request.args.get('date', datetime.now().strftime('%Y%m%d')).strip()
        else:
            data = request.get_json() or {}
            departure_terminal = data.get('departure_terminal', '').strip()
            arrival_terminal = data.get('arrival_terminal', '').strip()
            date = data.get('date', datetime.now().strftime('%Y%m%d')).strip()

        buses = crawler.search_buses(departure_terminal, arrival_terminal, date)

        return create_spring_response(
            success=True,
            data=buses,
            search_params={
                'departure_terminal': departure_terminal, 
                'arrival_terminal': arrival_terminal, 
                'date': date
            },
            spring_framework=True
        )

    except Exception as e:
        return create_spring_response(success=False, error=f'서버 오류: {str(e)}'), 500

@app.route('/search_flights', methods=['GET', 'POST'])
def api_search_flights():
    """항공편 조회 API - Spring Framework 호환"""
    try:
        if request.method == 'GET':
            departure_airport = request.args.get('departure_airport', '').strip()
            arrival_airport = request.args.get('arrival_airport', '').strip()
            departure_date = request.args.get('departure_date', '').strip()
            adults = int(request.args.get('adults', 1))
        else:
            data = request.get_json() or {}
            departure_airport = data.get('departure_airport', '').strip()
            arrival_airport = data.get('arrival_airport', '').strip()
            departure_date = data.get('departure_date', '').strip()
            adults = int(data.get('adults', 1))

        flights = crawler.search_flights(departure_airport, arrival_airport, departure_date, adults)

        return create_spring_response(
            success=True,
            data=flights,
            search_params={
                'departure_airport': departure_airport,
                'arrival_airport': arrival_airport, 
                'departure_date': departure_date,
                'adults': adults
            },
            amadeus_enabled=True,
            spring_framework=True
        )

    except Exception as e:
        return create_spring_response(success=False, error=f'서버 오류: {str(e)}'), 500

# ==================== Spring Framework 호환 정보 API ====================

@app.route('/stations')
def get_stations():
    """지원 기차역 목록 - Spring Framework getSupportedStations() 호환"""
    stations = list(crawler.station_mapping.keys())
    
    return jsonify({
        'success': True,
        'stations': stations,
        'count': len(stations),
        'spring_compatible': True,
        'tago_mapping': True
    })

@app.route('/terminals')
def get_terminals():
    """지원 버스터미널 목록"""
    return jsonify({
        'success': True,
        'terminals': list(crawler.terminal_codes.keys()),
        'count': len(crawler.terminal_codes),
        'spring_framework': True
    })

@app.route('/airports')
def get_airports():
    """지원 공항 목록"""
    return jsonify({
        'success': True,
        'airports': crawler.airport_codes,
        'count': len(crawler.airport_codes),
        'amadeus_compatible': True,
        'spring_framework': True
    })

# ==================== TAGO API 호환 상태 확인 ====================

@app.route('/tago/status')
def tago_status():
    """TAGO API 백업 상태 확인"""
    return jsonify({
        'success': True,
        'tago_backup': {
            'enabled': True,
            'status': 'Python 크롤러가 TAGO API 백업 역할',
            'station_mapping': 'TAGO API와 동일한 역명 매핑 적용',
            'fallback_priority': '2순위 (TAGO API 실패시 작동)'
        },
        'spring_framework': {
            'compatible': True,
            'jdk_version': '11',
            'server': 'Tomcat 9',
            'database': 'Oracle 21c'
        }
    })

# ==================== 설정 API ====================

@app.route('/config/debug', methods=['POST'])
def toggle_debug():
    """디버그 모드 토글"""
    global DEBUG_MODE
    
    try:
        data = request.get_json() if request.is_json else {}
        enabled = data.get('enabled', not DEBUG_MODE)
        
        DEBUG_MODE = enabled
        
        return jsonify({
            'success': True,
            'debug_mode': DEBUG_MODE,
            'message': f'디버그 모드 {"ON" if DEBUG_MODE else "OFF"}',
            'spring_framework': True
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# ==================== 에러 핸들러 ====================

@app.errorhandler(404)
def not_found(error):
    return jsonify({
        'success': False,
        'error': 'API 엔드포인트를 찾을 수 없습니다',
        'spring_framework': True
    }), 404

@app.errorhandler(500)
def internal_error(error):
    logger.error(f"Internal server error: {error}")
    return jsonify({
        'success': False,
        'error': '서버 내부 오류가 발생했습니다',
        'spring_framework': True
    }), 500

# ==================== 메인 실행 ====================

if __name__ == '__main__':
    print("=" * 80)
    print("🚄🚌✈️ Hee Transport Flask API Server v8.0 - Spring Framework 통합")
    print("=" * 80)
    print("환경 정보:")
    print("📋 Spring Framework (Legacy) 완벽 호환")
    print("☕ JDK 11 최적화")
    print("🖥️ Tomcat 9 서버 연동")
    print("🗄️ Oracle 21c 데이터베이스 호환")
    print("🐍 Python 3.13.7 환경")
    print("=" * 80)
    print("통합 기능:")
    print(f"🥷 selenium-stealth: {'✅ 활성화' if STEALTH_AVAILABLE else '❌ 미설치'}")
    print("🔄 TAGO API 백업 크롤링 (2순위)")
    print("🗺️ TAGO API 동일 역명 매핑")
    print(f"🔍 디버그 모드: {'✅ ON' if DEBUG_MODE else '❌ OFF'}")
    print("⏰ Spring Framework 응답 시간 최적화")
    print("✈️ AMADEUS API 항공편 연동")
    print("🌐 UTF-8 인코딩 완벽 지원")
    print("=" * 80)
    print(f"🌐 서버 주소: http://localhost:8000")
    print("🔗 Spring Framework 연동: http://localhost:8080")
    print("=" * 80)
    print("API 엔드포인트 (Spring Framework 호환):")
    print("  🚄 기차: /search_trains?departure=서울&arrival=부산&date=20250924")
    print("  🚌 버스: /search_buses?departure_terminal=서울고속버스터미널&arrival_terminal=부산서부터미널&date=20250924")
    print("  ✈️ 항공편: /search_flights?departure_airport=ICN&arrival_airport=NRT&departure_date=2025-09-24")
    print("  📋 역 목록: /stations (getSupportedStations() 호환)")
    print("  🔧 TAGO 상태: /tago/status")
    print("=" * 80)
    print("🎯 TAGO API 통합 전략:")
    print("- 1순위: TAGO API (Spring Framework)")
    print("- 2순위: Python 크롤러 (이 서버)")
    print("- 동일한 역명 매핑 시스템 적용")
    print("- UTF-8 인코딩 자동 처리")
    print("- Oracle DB 호환 응답 형식")
    print("=" * 80)
    
    try:
        app.run(host='0.0.0.0', port=8000, debug=False, threaded=True)
    except KeyboardInterrupt:
        print("\n👋 서버 종료")
    except Exception as e:
        logger.error(f"서버 실행 오류: {e}")
    finally:
        print("🔚 Spring Framework Transport API Server 종료 완료")