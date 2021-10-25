package com.terais.avsb.core;
/**
 * 이 클래스는 라이센스키를 복호화해서 알맞은 라이센스키인지 확인해주는 클래스이다.
 * @exception - 잘못된입력
 * @author JongChan Lee
 * @version v1.0
 * */

public class License {

	//시리얼 복호화
	private static long getChecksum(String generatedKey) {
        long checksum = 3L;
        for(int i=1; i<=generatedKey.length(); i++) {
            if(i != 9) {
                checksum += (Long.parseLong(generatedKey.substring(i-1,i))^(2*checksum));
            }
        }
        return checksum % 10;
    }
	//시리얼 체크
	protected boolean checkLicense(String serial){
		if(getChecksum(serial) == 8){
			return true;
		}else{
			return false;
		}
	}

}
