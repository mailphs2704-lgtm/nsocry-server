# Báo cáo build/test Windows NSOCry

- Trạng thái: **SUCCESS**
- Nhánh: agent/document-nsokiss-runtime
- Commit được kiểm tra: bde33f8baed7371f8eba1b2e399ce2c5c509af9f
- Bắt đầu UTC: 2026-09-11T15:15:02.1703477Z
- Kết thúc UTC: 2026-09-11T15:15:34.5173585Z
- Maven exit code: 0
- Tổng hợp test: Tests run: 386, Failures: 0, Errors: 0, Skipped: 0
- Java: openjdk version "17.0.20.1" 2026-08-18
- Maven: Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
- Lệnh: mvn clean package
- Database changed: false
- DATA imported: false
- Runtime snapshot published: false
- Server startup wired: false

## Ý nghĩa

Báo cáo này do NSOCRY_WORK.bat tạo trên máy Windows của chủ dự án. Báo cáo xác nhận khả năng compile/package và kết quả test của đúng commit nêu trên. Quy trình không chạy migration, không import DATA và không khởi động server.

## Nhật ký đầy đủ

Nhật ký Maven đầy đủ được giữ cục bộ tại .nsocry-work/maven-latest.log để tránh làm repository phình lớn. Khi build lỗi, phần cuối log được chép dưới đây.

## Phần cuối Maven log

    Progress (3): 188/328 kB | 73/272 kB | 40/488 kB
    Progress (3): 204/328 kB | 73/272 kB | 40/488 kB
    Progress (3): 221/328 kB | 73/272 kB | 40/488 kB
    Progress (3): 221/328 kB | 90/272 kB | 40/488 kB
    Progress (3): 237/328 kB | 90/272 kB | 40/488 kB
    Progress (3): 253/328 kB | 90/272 kB | 40/488 kB
    Progress (3): 270/328 kB | 90/272 kB | 40/488 kB
    Progress (3): 286/328 kB | 90/272 kB | 40/488 kB
    Progress (3): 303/328 kB | 90/272 kB | 40/488 kB
    Progress (3): 319/328 kB | 90/272 kB | 40/488 kB
    Progress (3): 328 kB | 90/272 kB | 40/488 kB    
                                                
    Downloaded from central: https://repo.maven.apache.org/maven2/org/jdom/jdom2/2.0.6.1/jdom2-2.0.6.1.jar (328 kB at 4.3 MB/s)
    Progress (3): 90/272 kB | 40/488 kB | 7.7/52 kB
    Progress (3): 90/272 kB | 40/488 kB | 7.7/52 kB
    Progress (3): 90/272 kB | 40/488 kB | 24/52 kB 
    Progress (3): 90/272 kB | 40/488 kB | 40/52 kB
    Progress (3): 90/272 kB | 40/488 kB | 52 kB   
                                               
    Downloaded from central: https://repo.maven.apache.org/maven2/org/ow2/asm/asm-tree/9.9.1/asm-tree-9.9.1.jar (52 kB at 577 kB/s)
    Progress (2): 90/272 kB | 57/488 kB
    Progress (2): 90/272 kB | 73/488 kB
    Progress (2): 90/272 kB | 90/488 kB
    Progress (2): 90/272 kB | 106/488 kB
    Progress (2): 90/272 kB | 122/488 kB
    Progress (2): 90/272 kB | 139/488 kB
    Progress (2): 90/272 kB | 155/488 kB
    Progress (2): 90/272 kB | 164/488 kB
    Progress (2): 90/272 kB | 180/488 kB
    Progress (2): 90/272 kB | 197/488 kB
    Progress (2): 90/272 kB | 213/488 kB
    Progress (2): 90/272 kB | 229/488 kB
    Progress (2): 90/272 kB | 246/488 kB
    Progress (2): 90/272 kB | 262/488 kB
    Progress (2): 90/272 kB | 279/488 kB
    Progress (2): 90/272 kB | 295/488 kB
    Progress (2): 90/272 kB | 311/488 kB
    Progress (2): 90/272 kB | 328/488 kB
    Progress (2): 90/272 kB | 344/488 kB
    Progress (2): 90/272 kB | 360/488 kB
    Progress (2): 90/272 kB | 377/488 kB
    Progress (2): 90/272 kB | 393/488 kB
    Progress (2): 90/272 kB | 410/488 kB
    Progress (2): 90/272 kB | 426/488 kB
    Progress (2): 90/272 kB | 442/488 kB
    Progress (2): 90/272 kB | 459/488 kB
    Progress (2): 90/272 kB | 475/488 kB
    Progress (2): 90/272 kB | 488 kB    
                                    
    Downloaded from central: https://repo.maven.apache.org/maven2/org/vafer/jdependency/2.15/jdependency-2.15.jar (488 kB at 4.3 MB/s)
    Progress (1): 106/272 kB
    Progress (1): 122/272 kB
    Progress (1): 139/272 kB
    Progress (1): 155/272 kB
    Progress (1): 172/272 kB
    Progress (1): 188/272 kB
    Progress (1): 204/272 kB
    Progress (1): 221/272 kB
    Progress (1): 237/272 kB
    Progress (1): 253/272 kB
    Progress (1): 270/272 kB
    Progress (1): 272 kB    
                        
    Downloaded from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-utils/3.6.0/plexus-utils-3.6.0.jar (272 kB at 2.2 MB/s)
    [WARNING] mariadb-java-client-3.5.10.jar, nsocry-server-0.1.0-SNAPSHOT.jar define 1 overlapping resource: 
    [WARNING]   - META-INF/MANIFEST.MF
    [WARNING] maven-shade-plugin has detected that some files are
    [WARNING] present in two or more JARs. When this happens, only one
    [WARNING] single version of the file is copied to the uber jar.
    [WARNING] Usually this is not harmful and you can skip these warnings,
    [WARNING] otherwise try to manually exclude artifacts based on
    [WARNING] mvn dependency:tree -Ddetail=true and the above output.
    [WARNING] See https://maven.apache.org/plugins/maven-shade-plugin/
    [INFO] Replacing original artifact with shaded artifact.
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time:  29.381 s
    [INFO] Finished at: 2026-09-11T22:15:34+07:00
    [INFO] ------------------------------------------------------------------------
