# LockModeType
* OPTIMISTIC
  * 트랜잭션 중 변경사항이 있을때
    * SELECT : 이때 버전을 같이 가져옴(해당 필드에 @Version, org.springframework.data.annotation.Version 가 아니라 javax.persistence.Version)
    * UPDATE : 이때 앞에서 Select한 version을 where에, set절에는 version=version+1 으로 version 업데이트
      * UPDATE 된 값이 없으면 중간에 버전이 변경된것으로 파악하여 에러 발생
  * 트랜잭션 중 변경사항이 없을때
    * SELECT : 이때 버전을 같이 가져옴(해당 필드에 @Version, org.springframework.data.annotation.Version 가 아니라 javax.persistence.Version)
    * SELECT : 이때 앞에서 Select한 version을 where에, 조회된 결과가 없으면 에러 발생
  * 수정을 하게 되면 version은 한개씩 올라감, 수정을 하지 않으면 version은 올라가지 않음
  
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
  * @Version이 없는 경우
    * 트랜잭션 중 변경사항이 없을때
      * SELECT FOR UPDATE
    * 트랜잭션 중 변경사항이 있을때
      * SELECT FOR UPDATE - UPDATE
  * @Version이 있는 경우
    * 트랜잭션 중 변경사항이 없을때
      * SELECT FOR UPDATE
    * 트랜잭션 중 변경사항이 있을때
      * SELECT FOR UPDATE - UPDATE(version도 함께 올라감)

* PESSIMISTIC_WRITE
  * @Version이 없는 경우
    * 트랜잭션 중 변경사항이 없을때
      * SELECT FOR UPDATE
    * 트랜잭션 중 변경사항이 있을때
      * SELECT FOR UPDATE - UPDATE
  * @Version이 있는 경우
    * 트랜잭션 중 변경사항이 없을때
      * SELECT FOR UPDATE
    * 트랜잭션 중 변경사항이 있을때
      * SELECT FOR UPDATE - UPDATE(version도 함께 올라감)

* PESSIMISTIC_FORCE_INCREMENT
  * @Version이 없는 경우
    * 에러 발생
  * @Version이 있는 경우
    * 트랜잭션 중 변경사항이 없을때
      * SELECT FOR UPDATE - update version
    * 트랜잭션 중 변경사항이 있을때
      * SELECT FOR UPDATE - update version - updateUPDATE(version도 함께 올라감)

* NONE
