package com.terais.avsb.core;
/**
 * 이 클래스는 라이센스키를 복호화해서 알맞은 라이센스키인지 확인해주는 클래스이다.
 * @author JongChan Lee
 * @version v1.0
 * */

public class License {	
	
	/**
	  * 시리얼 복호화
	  * @param generatedKey 라이센스 번호
	  * @return 라이센스 인증 정보
	  */
	private static long getChecksum(String generatedKey) {
        long checksum = 3L;
        for(int i=1; i<=generatedKey.length(); i++) {
            if(i != 9) {
                checksum += (Long.parseLong(generatedKey.substring(i-1,i))^(2*checksum));
            }
        }
        return checksum % 10;
    }
	
	/**
	  * 시리얼 체크
	  * @param serial 라이센스 번호
	  * @return 라이센스 유효성 여부
	  */
	protected boolean checkLicense(String serial){
		if(getChecksum(serial) == 8){
			return true;
		}else{
			return false;
		}
	}

}
