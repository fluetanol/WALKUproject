# Android
워쿠 (Android) - 건국대 모바일프로그래밍 4팀

# 전체적으로 개선해야할 사항
- [Paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview?hl=ko) 라이브러리를 사용하여 홈 탭에서 중복되는 네트워크 요청을 제거
- [MVVM](https://developer.android.com/topic/libraries/architecture?hl=ko) 패턴으로 view의 무게를 줄이고 독립된 viewmodel 클래스를 통해 전체적인 리팩토링 필요
- [Jetpack Compose](https://developer.android.com/jetpack/compose?gclid=CjwKCAjw46CVBhB1EiwAgy6M4vSmFZy7VtXH0MdCp67pTF-PY-VSR6wj0tWtCT2rsKKakhULVm1yyRoCn7EQAvD_BwE&gclsrc=aw.ds) 부분적인 compose로의 migration을 통해 UI 디자인 및 애니메이션 개선 필요 
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android?hl=ko) 를 통해 HomeFragment 클래스를 싱글톤으로 유지하고, 전체적인 코드에 DI를 사용하여 리팩토링 필요 

# 정태승 - Challenge 파트 개선사항
- 시간 부족으로 인해 kakao api 로그인 key를 통한 계정 연동을 못했음.
- timer 객체의 지나친 낭비, challenge를 너무 늘릴경우 오버헤드 발생할 요지가 있음 <- 의문점: viewmodel+ Livedata를 이용하며,service 클래스를 이용한 방식으로 최적화가 가능한가?
- 매 초마다 firebase의 내용을 업데이트 하고 가져오는 성능 저하 문제. 네트워크 의존도가 심각 <- 만약에 대비하여 local에서도 timer나 progressbar의 동작이 이루어질 수 있도록 따로 구현을 해볼 필요가 있음.
- 너무 단순한 challenge내용들. 종류를 좀 더 다양하게 추가해볼 필요가 있다고 생각
- 
# 시연 영상
[유튜브 링크](https://www.youtube.com/watch?v=Wi_6a4kydJc&feature=youtu.be)
