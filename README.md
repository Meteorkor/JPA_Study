# JPA_Study
 * 먼저 readme를 작성하여 시나리오를 작성 후 조금씩 코드 작성 예정

# LockModeType
* OPTIMISTIC
 * 트랜잭션 중 변경사항이 있을때
   * SELECT : 이때 버전을 같이 가져옴(해당 필드에 @Version, org.springframework.data.annotation.Version 가 아니라 javax.persistence.Version)
   * UPDATE : 이때 앞에서 Select한 version을 where에, set절에는 version=version+1 으로 version 업데이트
     * UPDATE 된 값이 없으면 중간에 버전이 변경된것으로 파악하여 에러 발생
 * 트랜잭션 중 변경사항이 없을때
   * SELECT : 이때 버전을 같이 가져옴(해당 필드에 @Version, org.springframework.data.annotation.Version 가 아니라 javax.persistence.Version)
   * SELECT : 이때 앞에서 Select한 version을 where에, 조회된 결과가 없으면 에러 발생
 * 수정을 하게 되면 version은 한개씩 올라감
 * 수정을 하지 않으면 version은 올라가지 않음
 
* OPTIMISTIC_FORCE_INCREMENT
 * 트랜잭션 중 변경사항이 있을때
   * SELECT : 이때 버전을 같이 가져옴(해당 필드에 @Version, org.springframework.data.annotation.Version 가 아니라 javax.persistence.Version)
   * UPDATE : 이때 앞에서 Select한 version을 where에, set절에는 version=version+1 으로 version 업데이트
     * UPDATE 된 값이 없으면 중간에 버전이 변경된것으로 파악하여 에러 발생
   * UPDATE : Version을 한번 더 업데이트
 * 트랜잭션 중 변경사항이 없을때
   * SELECT : 이때 버전을 같이 가져옴(해당 필드에 @Version, org.springframework.data.annotation.Version 가 아니라 javax.persistence.Version)
   * UPDATE : Version을 업데이트
 * 수정을 하게 되면 version은 두개씩 올라감
 * 수정을 하지 않으면 version은 한개씩 올라감

* READ(OPTIMISETIC과 동작 같음)
* WRITE(OPTIMISTIC_FORCE_INCREMENT 과 동작 같음)
* PESSIMISTIC_READ
* PESSIMISTIC_WRITE
* PESSIMISTIC_FORCE_INCREMENT
* NONE

  

# 선후처리 체크

