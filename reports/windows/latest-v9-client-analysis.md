# Phân tích bytecode client V9 Windows

- Tested commit: 7d2936a34f1363748542c61c53d77c4bf74fe7e2
- Client file: V9_NsoCry_x1.jar
- Client size: 1573045
- Client SHA-256: a6dc5c4a6f5314ddd9d8c8f0e03a9077dc3668775533e658562edeb9abe4a3ae
- Archive entries: 584
- Class count: 244
- Network candidates: 30
- Client JAR committed: false
- Database changed: false

## Manifest

```text
Manifest-Version: 1.0
Created-By: AdvMenu Unpacker (By Quyetdaik)
MIDlet-1: V9_X1,/icon.png,GameMidlet
MIDlet-Vendor: nsocry.com
NST-GameID: V9_X1_412
MIDlet-Version: 1.0.0
MIDlet-Name: V9_X1
MicroEdition-Configuration: CLDC-1.1
MicroEdition-Profile: MIDP-2.0
```

## Candidate classes

- by
- f
- O
- bR
- al
- cE
- cx
- ba
- aM
- cm
- aj
- aN
- ds
- dg
- dt
- cL
- ay
- GameMidlet
- y
- bY
- N
- aa
- cM
- do
- cX
- ab
- ao
- bC
- bP
- cK

## Bytecode evidence

```text
### f
      1658: iaload
      1659: invokevirtual #779                // Method N.b:(LcS;I)V
      1662: ldc2_w        #401                // long 500l
      1665: invokestatic  #587                // Method dt.k:(J)V
      1668: return
      1669: invokestatic  #486                // Method dg.bX:()Z
      1672: ifne          1812
      1675: getstatic     #665                // Field cM.jD:I
>     1678: tableswitch   { // 0 to 3
  
                       0: 1708
  
                       1: 1747
  
                       2: 1784
  
                       3: 1787
                 default: 1784
            }
      1708: invokestatic  #274                // Method N.f:()LN;
      1711: getfield      #459                // Field N.cn:I
      1714: aload_2
      1715: getfield      #271                // Field N.br:I
      1718: if_icmpne     1734
      1721: invokestatic  #274                // Method N.f:()LN;
      Code:
         0: getstatic     #134                // Field $op_ZbqfY0:I
         3: getstatic     #134                // Field $op_ZbqfY0:I
         6: if_icmpeq     13
         9: getstatic     #134                // Field $op_ZbqfY0:I
        12: pop
        13: aload_0
        14: getfield      #529                // Field b:B
>       17: lookupswitch  { // 3
  
                       4: 52
  
                      60: 52
  
                      61: 52
                 default: 171
            }
        52: invokestatic  #274                // Method N.f:()LN;
        55: getfield      #271                // Field N.br:I
        58: aload_0
        59: getfield      #947                // Field a:Ljava/io/DataInputStream;
        62: invokevirtual #1135               // Method java/io/DataInputStream.readInt:()I
        65: if_icmpne     171
        68: invokestatic  #1139               // Method cc.Z:()I
        71: istore_1
### al
         0: getstatic     #197                // Field $op_tgaLUZ:I
         3: getstatic     #197                // Field $op_tgaLUZ:I
         6: if_icmpeq     13
         9: getstatic     #197                // Field $op_tgaLUZ:I
        12: pop
        13: aload_0
        14: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
        17: invokevirtual #203                // Method java/io/DataInputStream.readByte:()B
>       20: lookupswitch  { // 2
  
                    -124: 48
  
                       2: 119
                 default: 122
            }
        48: aload_0
        49: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
        52: invokevirtual #225                // Method java/io/DataInputStream.readUTF:()Ljava/lang/String;
        55: astore_1
        56: aload_0
        57: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
        60: invokevirtual #225                // Method java/io/DataInputStream.readUTF:()Ljava/lang/String;
        63: getstatic     #409                // Field $s_hkFQsZ:Ljava/lang/String;
        66: aload_1
        67: invokestatic  #1073               // Method java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        20: istore_1
        21: aload_0
        22: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
        25: invokevirtual #212                // Method java/io/DataInputStream.readUnsignedByte:()I
        28: istore_3
        29: aconst_null
        30: astore_2
        31: iload_1
>       32: tableswitch   { // 2 to 39
  
                       2: 200
  
                       3: 209
  
                       4: 602
  
                       5: 614
  
                       6: 626
  
                       7: 635
  
                       8: 644
  
                       9: 653
        39: invokespecial #676                // Method java/lang/StringBuilder."<init>":(Ljava/lang/String;)V
        42: aload_1
        43: getfield      #1472               // Field bR.Y:B
        46: invokevirtual #680                // Method java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
        49: invokevirtual #895                // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        52: invokestatic  #1474               // Method dt.ak:(Ljava/lang/String;)V
        55: aload_1
        56: getfield      #1472               // Field bR.Y:B
>       59: tableswitch   { // -30 to 126
  
                     -30: 700
  
                     -29: 714
  
                     -28: 727
  
                     -27: 741
  
                     -26: 750
  
                     -25: 1098
  
                     -24: 1156
  
                     -23: 1616
         0: getstatic     #197                // Field $op_tgaLUZ:I
         3: getstatic     #197                // Field $op_tgaLUZ:I
         6: if_icmpeq     13
         9: getstatic     #197                // Field $op_tgaLUZ:I
        12: pop
        13: aload_1
        14: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
        17: invokevirtual #203                // Method java/io/DataInputStream.readByte:()B
>       20: tableswitch   { // -126 to -62
  
                    -126: 305
  
                    -125: 296
  
                    -124: 296
  
                    -123: 966
  
                    -122: 1325
  
                    -121: 1488
  
                    -120: 1651
  
                    -119: 1834
         0: getstatic     #197                // Field $op_tgaLUZ:I
         3: getstatic     #197                // Field $op_tgaLUZ:I
         6: if_icmpeq     13
         9: getstatic     #197                // Field $op_tgaLUZ:I
        12: pop
        13: aload_1
        14: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
        17: invokevirtual #203                // Method java/io/DataInputStream.readByte:()B
>       20: tableswitch   { // -128 to 115
  
                    -128: 1012
  
                    -127: 1082
  
                    -126: 3489
  
                    -125: 3663
  
                    -124: 4103
  
                    -123: 4250
  
                    -122: 4370
  
                    -121: 4392
### cx
         0: getstatic     #27                 // Field $op_OMc3se:I
         3: getstatic     #27                 // Field $op_OMc3se:I
         6: if_icmpeq     13
         9: getstatic     #27                 // Field $op_OMc3se:I
        12: pop
        13: aload_1
        14: invokevirtual #33                 // Method bR.a:()Ljava/io/DataInputStream;
        17: invokevirtual #39                 // Method java/io/DataInputStream.readByte:()B
>       20: tableswitch   { // 0 to 2
  
                       0: 48
  
                       1: 345
  
                       2: 558
                 default: 774
            }
        48: aload_1
        49: astore        5
        51: aload         5
        53: invokevirtual #33                 // Method bR.a:()Ljava/io/DataInputStream;
        56: invokevirtual #43                 // Method java/io/DataInputStream.readInt:()I
        59: istore_2
        60: invokestatic  #49                 // Method N.f:()LN;
        63: getfield      #52                 // Field N.br:I
### ba
       481: invokevirtual #1336               // Method do.a:(Ldp;Ljava/lang/String;III)V
       484: iinc          3, 1
       487: goto          189
       490: getstatic     #964                // Field ga:I
       493: ifle          782
       496: invokestatic  #1354               // Method N.f:()LN;
       499: getfield      #1603               // Field N.a:Lch;
       502: getfield      #1606               // Field ch.j:I
>      505: tableswitch   { // 0 to 6
  
                       0: 548
  
                       1: 623
  
                       2: 703
  
                       3: 623
  
                       4: 703
  
                       5: 623
  
                       6: 703
                 default: 782
            }
        12: pop
        13: aload_0
        14: invokestatic  #1473               // Method c:(Ldp;)V
        17: getstatic     #3273               // Field gW:I
        20: getstatic     #590                // Field $np_cKWQgx:[I
        23: bipush        14
        25: iaload
        26: idiv
>       27: tableswitch   { // 0 to 4
  
                       0: 60
  
                       1: 67
  
                       2: 74
  
                       3: 81
  
                       4: 88
                 default: 95
            }
        60: getstatic     #1537               // Field do.l:Ldo;
        63: astore_1
        64: goto          99
        67: getstatic     #1529               // Field do.m:Ldo;
    private static void F(int);
      Code:
         0: getstatic     #1319               // Field $op_PZ7gDb:I
         3: getstatic     #1319               // Field $op_PZ7gDb:I
         6: if_icmpeq     13
         9: getstatic     #1319               // Field $op_PZ7gDb:I
        12: pop
        13: iload_0
>       14: tableswitch   { // 1 to 3
  
                       1: 40
  
                       2: 52
  
                       3: 65
                 default: 76
            }
        40: invokestatic  #2013               // Method cK.a:()LcK;
        43: getstatic     #590                // Field $np_cKWQgx:[I
        46: iconst_3
        47: iaload
        48: invokevirtual #3897               // Method cK.as:(I)V
        51: return
        52: invokestatic  #2013               // Method cK.a:()LcK;
        55: getstatic     #590                // Field $np_cKWQgx:[I
        13: invokestatic  #1354               // Method N.f:()LN;
        16: getfield      #2819               // Field N.d:LN;
        19: ifnull        276
        22: invokestatic  #1354               // Method N.f:()LN;
        25: getfield      #2819               // Field N.d:LN;
        28: invokevirtual #3339               // Method N.au:()Z
        31: ifne          276
        34: iload_1
>       35: tableswitch   { // 1 to 10
  
                       1: 88
  
                       2: 104
  
                       3: 120
  
                       4: 136
  
                       5: 152
  
                       6: 166
  
                       7: 182
  
                       8: 209
    private static void en();
      Code:
         0: getstatic     #1319               // Field $op_PZ7gDb:I
         3: getstatic     #1319               // Field $op_PZ7gDb:I
         6: if_icmpeq     13
         9: getstatic     #1319               // Field $op_PZ7gDb:I
        12: pop
        13: getstatic     #968                // Field gc:I
>       16: tableswitch   { // 0 to 29
  
                       0: 152
  
                       1: 211
  
                       2: 270
  
                       3: 329
  
                       4: 353
  
                       5: 377
  
                       6: 401
  
                       7: 425
         9: getstatic     #1319               // Field $op_PZ7gDb:I
        12: pop
        13: invokestatic  #4892               // Method dv.a:()Ldv;
        16: iload_1
        17: aload_2
        18: invokevirtual #4895               // Method dv.a:(ILjava/lang/Object;)Z
        21: ifne          10684
        24: iload_1
>       25: lookupswitch  { // 421
  
                       1: 3404
  
                       2: 3408
  
                       3: 3426
  
                     222: 3431
  
                     333: 3436
  
                     334: 3448
  
                     335: 3453
  
                     336: 3474
         6: if_icmpeq     13
         9: getstatic     #1319               // Field $op_PZ7gDb:I
        12: pop
        13: getstatic     #966                // Field gb:I
        16: ifge          21
        19: aconst_null
        20: areturn
        21: iload_0
>       22: tableswitch   { // 2 to 52
  
                       2: 240
  
                       3: 260
  
                       4: 271
  
                       5: 282
  
                       6: 297
  
                       7: 317
  
                       8: 337
  
                       9: 357
        43: iaload
        44: if_icmpeq     1315
        47: getstatic     #970                // Field gd:I
        50: getstatic     #590                // Field $np_cKWQgx:[I
        53: bipush        12
        55: iaload
        56: if_icmpeq     1315
        59: getstatic     #970                // Field gd:I
>       62: tableswitch   { // 0 to 6
  
                       0: 104
  
                       1: 239
  
                       2: 415
  
                       3: 466
  
                       4: 508
  
                       5: 728
  
                       6: 839
                 default: 1257
            }
      2370: dup_x1
      2371: putfield      #670                // Field hr:I
      2374: getstatic     #590                // Field $np_cKWQgx:[I
      2377: iconst_3
      2378: iaload
      2379: invokevirtual #1336               // Method do.a:(Ldp;Ljava/lang/String;III)V
      2382: aload_0
      2383: getfield      #6006               // Field cG:I
>     2386: tableswitch   { // 1 to 8
  
                       1: 2432
  
                       2: 2475
  
                       3: 2518
  
                       4: 2561
  
                       5: 2604
  
                       6: 2647
  
                       7: 2690
  
                       8: 2733
        16: iconst_3
        17: iaload
        18: putstatic     #964                // Field ga:I
        21: aload_0
        22: dup
        23: getfield      #800                // Field M:Lak;
        26: putfield      #916                // Field dr.h:Lak;
        29: iload_1
>       30: tableswitch   { // 2 to 53
  
                       2: 252
  
                       3: 293
  
                       4: 296
  
                       5: 293
  
                       6: 337
  
                       7: 375
  
                       8: 413
  
                       9: 451
### aj
        26: invokespecial #2700               // Method java/io/DataOutputStream."<init>":(Ljava/io/OutputStream;)V
        29: astore_1
        30: aload_1
        31: getstatic     #686                // Field C:Ljava/lang/String;
        34: ifnonnull     43
        37: ldc_w         #803                // String
        40: goto          46
        43: getstatic     #686                // Field C:Ljava/lang/String;
>       46: invokevirtual #2703               // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        49: aload_1
        50: getstatic     #692                // Field q:Lcg;
        53: invokevirtual #1246               // Method cg.size:()I
        56: invokevirtual #2706               // Method java/io/DataOutputStream.writeByte:(I)V
        59: getstatic     #653                // Field $np_eSWZNS:[I
        62: iconst_0
        63: iaload
        64: istore_2
        65: iload_2
        66: getstatic     #692                // Field q:Lcg;
        69: invokevirtual #1246               // Method cg.size:()I
        72: if_icmpge     95
        75: aload_1
        76: getstatic     #692                // Field q:Lcg;
        79: iload_2
        80: invokevirtual #1921               // Method cg.elementAt:(I)Ljava/lang/Object;
        83: checkcast     #831                // class java/lang/String
>       86: invokevirtual #2703               // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        89: iinc          2, 1
        92: goto          65
        95: aload_1
        96: invokevirtual #2709               // Method java/io/DataOutputStream.flush:()V
        99: aload_0
       100: invokevirtual #2710               // Method java/io/ByteArrayOutputStream.flush:()V
       103: getstatic     #1023               // Field $s_KxGcR8:Ljava/lang/String;
       106: aload_0
       107: invokevirtual #2714               // Method java/io/ByteArrayOutputStream.toByteArray:()[B
       110: invokestatic  #2444               // Method RMS.a:(Ljava/lang/String;[B)V
       113: return
       114: pop
       115: return
      Exception table:
         from    to  target type
            30   113   114   Class java/lang/Exception
        66: getstatic     #859                // Field et:I
        69: invokevirtual #2721               // Method java/io/DataOutputStream.writeInt:(I)V
        72: aload_1
        73: getstatic     #861                // Field E:Ljava/lang/String;
        76: ifnonnull     85
        79: ldc_w         #803                // String
        82: goto          88
        85: getstatic     #861                // Field E:Ljava/lang/String;
>       88: invokevirtual #2703               // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        91: aload_1
        92: getstatic     #851                // Field es:I
        95: invokevirtual #2721               // Method java/io/DataOutputStream.writeInt:(I)V
        98: getstatic     #1314               // Field $s_GiO8xY:Ljava/lang/String;
       101: aload_0
       102: invokevirtual #2714               // Method java/io/ByteArrayOutputStream.toByteArray:()[B
       105: invokestatic  #2724               // Method RMS.saveRMS:(Ljava/lang/String;[B)V
       108: return
       109: pop
       110: return
      Exception table:
         from    to  target type
            13   108   109   Class java/lang/Exception
  
    private static boolean aK();
      Code:
        41: i2b
        42: invokevirtual #2706               // Method java/io/DataOutputStream.writeByte:(I)V
        45: aload_1
        46: getstatic     #805                // Field D:Ljava/lang/String;
        49: ifnonnull     58
        52: ldc_w         #803                // String
        55: goto          61
        58: getstatic     #805                // Field D:Ljava/lang/String;
>       61: invokevirtual #2703               // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        64: aload_1
        65: getstatic     #807                // Field cc:Z
        68: invokevirtual #2718               // Method java/io/DataOutputStream.writeBoolean:(Z)V
        71: aload_1
        72: getstatic     #809                // Field cd:Z
        75: invokevirtual #2718               // Method java/io/DataOutputStream.writeBoolean:(Z)V
        78: aload_1
        79: invokevirtual #2709               // Method java/io/DataOutputStream.flush:()V
        82: getstatic     #3249               // Field $s_U3JCLp:Ljava/lang/String;
        85: aload_0
        86: invokevirtual #2714               // Method java/io/ByteArrayOutputStream.toByteArray:()[B
        89: invokestatic  #2444               // Method RMS.a:(Ljava/lang/String;[B)V
        92: return
        93: pop
        94: return
      Exception table:
### aN
       598: astore        4
       600: new           #452                // class java/io/DataOutputStream
       603: dup
       604: aload         4
       606: invokespecial #455                // Method java/io/DataOutputStream."<init>":(Ljava/io/OutputStream;)V
       609: astore        5
       611: aload         5
       613: getstatic     #171                // Field M:Ljava/lang/String;
>      616: invokevirtual #458                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
       619: aload         5
       621: getstatic     #177                // Field ff:I
       624: invokevirtual #462                // Method java/io/DataOutputStream.writeInt:(I)V
       627: aload         5
       629: getstatic     #183                // Field fg:I
       632: invokevirtual #462                // Method java/io/DataOutputStream.writeInt:(I)V
       635: aload         5
       637: getstatic     #189                // Field fh:I
       640: invokevirtual #462                // Method java/io/DataOutputStream.writeInt:(I)V
       643: aload         5
       645: getstatic     #319                // Field fi:I
       648: invokevirtual #462                // Method java/io/DataOutputStream.writeInt:(I)V
       651: aload         5
       653: getstatic     #165                // Field fj:I
       656: invokevirtual #462                // Method java/io/DataOutputStream.writeInt:(I)V
       659: aload         5
       763: aload         5
       765: getstatic     #306                // Field cP:Z
       768: invokevirtual #466                // Method java/io/DataOutputStream.writeBoolean:(Z)V
       771: aload         5
       773: getstatic     #308                // Field cQ:Z
       776: invokevirtual #466                // Method java/io/DataOutputStream.writeBoolean:(Z)V
       779: aload         5
       781: getstatic     #195                // Field N:Ljava/lang/String;
>      784: invokevirtual #458                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
       787: getstatic     #474                // Field $s_fHhAFs:Ljava/lang/String;
       790: aload         4
       792: invokevirtual #478                // Method java/io/ByteArrayOutputStream.toByteArray:()[B
       795: invokestatic  #484                // Method RMS.saveRMS:(Ljava/lang/String;[B)V
       798: getstatic     #121                // Field $np_PhHTXP:[I
       801: bipush        14
       803: iaload
       804: putstatic     #487                // Field aj.ew:I
       807: getstatic     #489                // Field $s_iiZvTf:Ljava/lang/String;
       810: invokestatic  #491                // Method aj.B:(Ljava/lang/String;)V
       813: aload         5
       815: invokevirtual #494                // Method java/io/DataOutputStream.flush:()V
       818: aload         4
       820: invokevirtual #495                // Method java/io/ByteArrayOutputStream.flush:()V
       823: aload         4
       825: invokevirtual #498                // Method java/io/ByteArrayOutputStream.close:()V
### dg
         3: getstatic     #268                // Field $op_Bl9bhQ:I
         6: if_icmpeq     13
         9: getstatic     #268                // Field $op_Bl9bhQ:I
        12: pop
        13: iload_0
        14: invokestatic  #498                // Method w:(I)Z
        17: ifeq          437
        20: iload_0
>       21: tableswitch   { // 91 to 159
  
                      91: 312
  
                      92: 319
  
                      93: 326
  
                      94: 329
  
                      95: 336
  
                      96: 343
  
                      97: 326
  
                      98: 326
       834: ior
       835: getstatic     #146                // Field $np_E1NCGe:[I
       838: sipush        166
       841: iaload
       842: ior
       843: istore        12
       845: goto          1355
       848: iload         9
>      850: lookupswitch  { // 11
  
                      80: 948
  
                      91: 978
  
                      94: 1008
  
                      98: 1039
  
                     104: 1091
  
                     105: 1150
  
                     113: 1181
  
                     114: 1234
### cL
    public int f;
  
    private byte ac;
  
    public static byte ae;
  
    private static int $op_AptWQu;
  
>   public javax.microedition.io.SocketConnection a;
  
    private java.io.DataOutputStream b;
  
    private final W a;
  
    public java.lang.String aw;
  
    private static java.lang.String $s_bSP9z9;
  
    public java.lang.Thread e;
  
    private static java.lang.String $s_6EJbjj;
  
    private boolean bI;
  
    public al b;
        70: ifne          123
        73: aload_0
        74: getstatic     #56                 // Field $np_SdBq9f:[I
        77: iconst_0
        78: iaload
        79: putfield      #185                // Field fl:Z
        82: aload_0
        83: aconst_null
>       84: putfield      #187                // Field a:Ljavax/microedition/io/SocketConnection;
        87: aload_0
        88: new           #189                // class java/lang/Thread
        91: dup
        92: new           #191                // class aa
        95: dup
        96: aload_0
        97: aload_1
        98: invokespecial #194                // Method aa."<init>":(LcL;Ljava/lang/String;)V
       101: invokespecial #197                // Method java/lang/Thread."<init>":(Ljava/lang/Runnable;)V
       104: putfield      #199                // Field e:Ljava/lang/Thread;
       107: getstatic     #204                // Field GameMidlet.F:B
       110: putstatic     #206                // Field ae:B
       113: invokestatic  #209                // Method ae.a:()V
       116: getstatic     #159                // Field $s_6EJbjj:Ljava/lang/String;
       119: invokestatic  #126                // Method dt.g:(Ljava/lang/String;)V
       122: return
        55: getstatic     #56                 // Field $np_SdBq9f:[I
        58: iconst_0
        59: iaload
        60: putfield      #118                // Field cS:Z
        63: aload_0
        64: getfield      #254                // Field ai:Lcg;
        67: invokevirtual #267                // Method cg.removeAllElements:()V
        70: aload_0
>       71: getfield      #187                // Field a:Ljavax/microedition/io/SocketConnection;
        74: ifnull        91
        77: aload_0
>       78: getfield      #187                // Field a:Ljavax/microedition/io/SocketConnection;
>       81: invokeinterface #272,  1          // InterfaceMethod javax/microedition/io/SocketConnection.close:()V
        86: aload_0
        87: aconst_null
>       88: putfield      #187                // Field a:Ljavax/microedition/io/SocketConnection;
        91: aload_0
        92: getfield      #223                // Field b:Ljava/io/DataOutputStream;
        95: ifnull        110
        98: aload_0
        99: getfield      #223                // Field b:Ljava/io/DataOutputStream;
       102: invokevirtual #273                // Method java/io/DataOutputStream.close:()V
       105: aload_0
       106: aconst_null
       107: putfield      #223                // Field b:Ljava/io/DataOutputStream;
       110: aload_0
       111: getfield      #275                // Field c:Ljava/io/DataInputStream;
       114: ifnull        129
       117: aload_0
       118: getfield      #275                // Field c:Ljava/io/DataInputStream;
       121: invokevirtual #278                // Method java/io/DataInputStream.close:()V
       124: aload_0
### GameMidlet
        27: iload_2
        28: getstatic     #161                // Field t:[Ljava/lang/String;
        31: arraylength
        32: if_icmpge     86
        35: aload_1
        36: getstatic     #161                // Field t:[Ljava/lang/String;
        39: iload_2
        40: aaload
>       41: invokevirtual #168                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        44: aload_1
        45: getstatic     #170                // Field u:[Ljava/lang/String;
        48: iload_2
        49: aaload
>       50: invokevirtual #168                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        53: aload_1
        54: getstatic     #172                // Field h:[S
        57: iload_2
        58: saload
        59: invokevirtual #175                // Method java/io/DataOutputStream.writeShort:(I)V
        62: aload_1
        63: getstatic     #177                // Field u:[B
        66: iload_2
        67: baload
        68: invokevirtual #165                // Method java/io/DataOutputStream.writeByte:(I)V
        71: aload_1
        72: getstatic     #179                // Field O:[I
        75: iload_2
        76: iaload
        77: invokevirtual #182                // Method java/io/DataOutputStream.writeInt:(I)V
        80: iinc          2, 1
### bY
        47: getstatic     #30                 // Field ag:Lcg;
        50: invokevirtual #88                 // Method cg.size:()I
        53: if_icmpge     76
        56: aload_1
        57: getstatic     #30                 // Field ag:Lcg;
        60: iload_2
        61: invokevirtual #92                 // Method cg.elementAt:(I)Ljava/lang/Object;
        64: checkcast     #77                 // class java/lang/String
>       67: invokevirtual #152                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        70: iinc          2, 1
        73: goto          46
        76: aload_1
        77: invokevirtual #155                // Method java/io/DataOutputStream.flush:()V
        80: aload_0
        81: invokevirtual #156                // Method java/io/ByteArrayOutputStream.flush:()V
        84: getstatic     #35                 // Field $s_EpJzgG:Ljava/lang/String;
        87: aload_0
        88: invokevirtual #160                // Method java/io/ByteArrayOutputStream.toByteArray:()[B
        91: invokestatic  #163                // Method RMS.a:(Ljava/lang/String;[B)V
        94: return
        95: pop
        96: return
      Exception table:
         from    to  target type
            30    94    95   Class java/lang/Exception
### N
      6996: iconst_3
      6997: iaload
      6998: isub
      6999: i2b
      7000: putfield      #917                // Field i:B
      7003: goto          9646
      7006: aload_0
      7007: getfield      #288                // Field bp:I
>     7010: tableswitch   { // 1 to 14
  
                       1: 7080
  
                       2: 7515
  
                       3: 9201
  
                       4: 9320
  
                       5: 9432
  
                       6: 9457
  
                       7: 9504
  
                       8: 9504
        29: dup
        30: astore_1
        31: ifnull        39
        34: aload_1
        35: getfield      #1646               // Field O.bk:I
        38: ireturn
        39: aload_0
        40: getfield      #994                // Field n:S
>       43: lookupswitch  { // 18
  
                       0: 196
  
                       4: 203
  
                       6: 211
  
                       8: 219
  
                       9: 227
  
                      17: 235
  
                      19: 243
  
                      21: 251
       136: aaload
       137: getfield      #1009               // Field ct.a:S
       140: ireturn
       141: aload_1
       142: getfield      #1646               // Field O.bk:I
       145: ireturn
       146: aload_0
       147: getfield      #996                // Field o:S
>      150: lookupswitch  { // 27
  
                       1: 376
  
                       3: 574
  
                       5: 773
  
                       7: 970
  
                      10: 1168
  
                      18: 1366
  
                      20: 1564
  
                      22: 1762
      6030: aaload
      6031: getfield      #1009               // Field ct.a:S
      6034: ireturn
      6035: aload_1
      6036: getfield      #1646               // Field O.bk:I
      6039: ireturn
      6040: aload_0
      6041: getfield      #996                // Field o:S
>     6044: lookupswitch  { // 27
  
                       1: 6272
  
                       3: 6466
  
                       5: 6659
  
                       7: 6854
  
                      10: 7050
  
                      18: 7246
  
                      20: 7442
  
                      22: 7638
      11813: aaload
      11814: getfield      #1009              // Field ct.a:S
      11817: ireturn
      11818: aload_1
      11819: getfield      #1646              // Field O.bk:I
      11822: ireturn
      11823: aload_0
      11824: getfield      #996               // Field o:S
>     11827: lookupswitch  { // 27
  
                       1: 12052
  
                       3: 12290
  
                       5: 12489
  
                       7: 12686
  
                      10: 12884
  
                      18: 13082
  
                      20: 13280
  
                      22: 13478
       132: aaload
       133: getfield      #1009               // Field ct.a:S
       136: ireturn
       137: aload_2
       138: getfield      #1646               // Field O.bk:I
       141: ireturn
       142: aload_0
       143: getfield      #992                // Field m:S
>      146: lookupswitch  { // 42
  
                       2: 492
  
                      11: 679
  
                      23: 867
  
                      24: 1056
  
                      25: 1245
  
                      26: 1435
  
                      27: 1623
  
                      28: 1811
      8699: aaload
      8700: getfield      #1009               // Field ct.a:S
      8703: ireturn
      8704: aload_2
      8705: getfield      #1646               // Field O.bk:I
      8708: ireturn
      8709: aload_0
      8710: getfield      #992                // Field m:S
>     8713: lookupswitch  { // 42
  
                       2: 9060
  
                      11: 9232
  
                      23: 9405
  
                      24: 9578
  
                      25: 9751
  
                      26: 9923
  
                      27: 10096
  
                      28: 10269
      16575: aaload
      16576: getfield      #1009              // Field ct.a:S
      16579: ireturn
      16580: aload_2
      16581: getfield      #1646              // Field O.bk:I
      16584: ireturn
      16585: aload_0
      16586: getfield      #992               // Field m:S
>     16589: lookupswitch  { // 42
  
                       2: 16936
  
                      11: 17124
  
                      23: 17312
  
                      24: 17501
  
                      25: 17690
  
                      26: 17880
  
                      27: 18068
  
                      28: 18256
### aa
        82: putfield      #112                // Field cL.bD:Z
        85: aload_0
        86: getfield      #29                 // Field u:Ljava/lang/String;
        89: astore_1
        90: aload_0
        91: getfield      #27                 // Field b:LcL;
        94: aload_1
        95: invokestatic  #118                // Method javax/microedition/io/Connector.open:(Ljava/lang/String;)Ljavax/microedition/io/Connection;
>       98: checkcast     #120                // class javax/microedition/io/SocketConnection
>      101: putfield      #124                // Field cL.a:Ljavax/microedition/io/SocketConnection;
       104: getstatic     #126                // Field $s_1JBw7z:Ljava/lang/String;
       107: invokestatic  #89                 // Method dt.g:(Ljava/lang/String;)V
       110: aload_0
       111: getfield      #27                 // Field b:LcL;
       114: aload_0
       115: getfield      #27                 // Field b:LcL;
>      118: getfield      #124                // Field cL.a:Ljavax/microedition/io/SocketConnection;
>      121: invokeinterface #130,  1          // InterfaceMethod javax/microedition/io/SocketConnection.openDataOutputStream:()Ljava/io/DataOutputStream;
       126: invokestatic  #133                // Method cL.a:(LcL;Ljava/io/DataOutputStream;)V
       129: aload_0
       130: getfield      #27                 // Field b:LcL;
       133: aload_0
       134: getfield      #27                 // Field b:LcL;
>      137: getfield      #124                // Field cL.a:Ljavax/microedition/io/SocketConnection;
>      140: invokeinterface #137,  1          // InterfaceMethod javax/microedition/io/SocketConnection.openDataInputStream:()Ljava/io/DataInputStream;
       145: putfield      #141                // Field cL.c:Ljava/io/DataInputStream;
       148: aload_0
       149: getfield      #27                 // Field b:LcL;
       152: new           #91                 // class java/lang/Thread
       155: dup
       156: aload_0
       157: getfield      #27                 // Field b:LcL;
       160: invokestatic  #144                // Method cL.a:(LcL;)LW;
       163: invokespecial #97                 // Method java/lang/Thread."<init>":(Ljava/lang/Runnable;)V
       166: dup_x1
       167: putfield      #147                // Field cL.h:Ljava/lang/Thread;
       170: invokevirtual #105                // Method java/lang/Thread.start:()V
       173: aload_0
       174: getfield      #27                 // Field b:LcL;
       177: new           #91                 // class java/lang/Thread
       180: dup
### ab
       694: getstatic     #49                 // Field $s_2yhCBG:Ljava/lang/String;
       697: invokestatic  #171                // Method dt.g:(Ljava/lang/String;)V
       700: aload_0
       701: getfield      #28                 // Field a:LcL;
       704: getfield      #139                // Field cL.b:Lal;
       707: invokevirtual #182                // Method al.cH:()V
       710: aload_0
       711: getfield      #28                 // Field a:LcL;
>      714: getfield      #185                // Field cL.a:Ljavax/microedition/io/SocketConnection;
       717: ifnull        734
       720: aload_0
       721: getfield      #28                 // Field a:LcL;
       724: invokestatic  #187                // Method cL.a:(LcL;)V
       727: return
       728: getstatic     #51                 // Field $s_Sht6Gv:Ljava/lang/String;
       731: invokestatic  #171                // Method dt.g:(Ljava/lang/String;)V
       734: return
      Exception table:
         from    to  target type
           541   576   579   Class java/lang/Exception
            13    23   583   Class java/lang/Exception
            26   580   583   Class java/lang/Exception
  
    private static java.lang.String $d_07c2UyBL(byte[]);
      Code:
### ao
        14: getfield      #80                 // Field w:Lcg;
        17: invokevirtual #172                // Method cg.size:()I
        20: ifle          339
        23: aload_0
        24: getfield      #166                // Field cK:Z
        27: ifne          314
        30: aload_0
        31: getfield      #89                 // Field r:B
>       34: tableswitch   { // 0 to 3
  
                       0: 64
  
                       1: 123
  
                       2: 182
  
                       3: 254
                 default: 313
            }
        64: aload_0
        65: dup
        66: getfield      #402                // Field q:B
        69: getstatic     #82                 // Field $np_4WgBnH:[I
        72: iconst_3
        73: iaload
### cK
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        75
        20: iaload
        21: invokestatic  #75                 // Method b:(B)LbR;
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_0
        34: getfield      #40                 // Field a:LcL;
        37: aload_2
        38: invokevirtual #62                 // Method cL.t:(LbR;)V
        41: aload_2
        42: invokevirtual #67                 // Method bR.hx:()V
        45: return
        46: pop
        47: aload_2
        48: invokevirtual #67                 // Method bR.hx:()V
        51: return
        52: astore_3
        53: aload_2
        54: invokevirtual #67                 // Method bR.hx:()V
        57: aload_3
        58: athrow
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        88
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_3
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_3
        38: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        41: getstatic     #54                 // Field $np_BDPyBd:[I
        44: iconst_4
        45: iaload
        46: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        49: aload_0
        50: getfield      #40                 // Field a:LcL;
        53: aload_3
        54: invokevirtual #62                 // Method cL.t:(LbR;)V
        57: aload_3
        58: invokevirtual #67                 // Method bR.hx:()V
        61: return
        62: pop
        63: aload_3
        64: invokevirtual #67                 // Method bR.hx:()V
        28: dup
        29: astore_3
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: iload_1
        34: invokevirtual #83                 // Method java/io/DataOutputStream.writeShort:(I)V
        37: aload_3
        38: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        41: aload_2
>       42: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        45: aload_0
        46: getfield      #40                 // Field a:LcL;
        49: aload_3
        50: invokevirtual #62                 // Method cL.t:(LbR;)V
        53: aload_3
        54: invokevirtual #67                 // Method bR.hx:()V
        57: return
        58: pop
        59: aload_3
        60: invokevirtual #67                 // Method bR.hx:()V
        63: return
        64: astore        4
        66: aload_3
        67: invokevirtual #67                 // Method bR.hx:()V
        70: aload         4
        72: athrow
        13: getstatic     #54                 // Field $np_BDPyBd:[I
        16: bipush        7
        18: iaload
        19: invokestatic  #212                // Method a:(B)LbR;
        22: dup
        23: astore_2
        24: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        27: aload_1
>       28: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        31: aload_0
        32: getfield      #40                 // Field a:LcL;
        35: aload_2
        36: invokevirtual #62                 // Method cL.t:(LbR;)V
        39: aload_2
        40: invokevirtual #67                 // Method bR.hx:()V
        43: return
        44: pop
        45: return
      Exception table:
         from    to  target type
            13    43    44   Class java/io/IOException
  
    public final void at(int);
      Code:
         0: getstatic     #52                 // Field $op_ZnP3P8:I
        98: astore_2
        99: aload_1
       100: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       103: aload_2
       104: ifnull        111
       107: aload_2
       108: goto          114
       111: getstatic     #183                // Field $s_ItEbuR:Ljava/lang/String;
>      114: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
       117: aload_1
       118: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       121: getstatic     #54                 // Field $np_BDPyBd:[I
       124: iconst_4
       125: iaload
       126: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
       129: aload_1
       130: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       133: getstatic     #54                 // Field $np_BDPyBd:[I
       136: iconst_4
       137: iaload
       138: invokevirtual #147                // Method java/io/DataOutputStream.writeInt:(I)V
       141: aload_1
       142: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       145: getstatic     #255                // Field df.j:I
       148: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
       152: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       155: getstatic     #54                 // Field $np_BDPyBd:[I
       158: iconst_4
       159: iaload
       160: invokevirtual #147                // Method java/io/DataOutputStream.writeInt:(I)V
       163: aload_1
       164: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       167: ldc_w         #257                // String 0
>      170: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
       173: aload_0
       174: getfield      #40                 // Field a:LcL;
       177: aload_1
       178: invokevirtual #62                 // Method cL.t:(LbR;)V
       181: aload_1
       182: invokevirtual #67                 // Method bR.hx:()V
       185: return
       186: pop
       187: return
      Exception table:
         from    to  target type
            13   185   186   Class java/io/IOException
  
    public final void if();
      Code:
         0: getstatic     #52                 // Field $op_ZnP3P8:I
        29: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        32: getstatic     #54                 // Field $np_BDPyBd:[I
        35: iconst_3
        36: iaload
        37: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        40: aload         4
        42: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        45: aload_1
>       46: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        49: aload         4
        51: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        54: iload_2
        55: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        58: aload         4
        60: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        63: iload_3
        64: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        67: goto          71
        70: pop
        71: aload_0
        72: getfield      #40                 // Field a:LcL;
        75: aload         4
        77: invokevirtual #62                 // Method cL.t:(LbR;)V
        80: return
      Exception table:
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        86
        20: iaload
        21: invokestatic  #58                 // Method c:(B)LbR;
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_0
        34: getfield      #40                 // Field a:LcL;
        37: aload_2
        38: invokevirtual #62                 // Method cL.t:(LbR;)V
        41: aload_2
        42: invokevirtual #67                 // Method bR.hx:()V
        45: return
        46: pop
        47: aload_2
        48: invokevirtual #67                 // Method bR.hx:()V
        51: return
        52: astore_3
        53: aload_2
        54: invokevirtual #67                 // Method bR.hx:()V
        57: aload_3
        58: athrow
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        68
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        96
        20: iaload
        21: invokestatic  #75                 // Method b:(B)LbR;
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_0
        34: getfield      #40                 // Field a:LcL;
        37: aload_2
        38: invokevirtual #62                 // Method cL.t:(LbR;)V
        41: aload_2
        42: invokevirtual #67                 // Method bR.hx:()V
        45: return
        46: pop
        47: aload_2
        48: invokevirtual #67                 // Method bR.hx:()V
        51: return
        52: astore_3
        53: aload_2
        54: invokevirtual #67                 // Method bR.hx:()V
        57: aload_3
        58: athrow
        45: getstatic     #54                 // Field $np_BDPyBd:[I
        48: bipush        90
        50: iaload
        51: invokespecial #92                 // Method bR."<init>":(B)V
        54: dup
        55: astore        4
        57: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        60: aload_1
>       61: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        64: aload         4
        66: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        69: iload_3
        70: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        73: aload_0
        74: getfield      #40                 // Field a:LcL;
        77: aload         4
        79: invokevirtual #62                 // Method cL.t:(LbR;)V
        82: aload         4
        84: invokevirtual #67                 // Method bR.hx:()V
        87: return
        88: pop
        89: aload         4
        91: invokevirtual #67                 // Method bR.hx:()V
        94: return
        95: astore        5
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        89
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        16: getstatic     #54                 // Field $np_BDPyBd:[I
        19: bipush        94
        21: iaload
        22: invokestatic  #58                 // Method c:(B)LbR;
        25: dup
        26: astore        4
        28: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        31: aload_1
>       32: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        35: aload         4
        37: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        40: aload_2
        41: arraylength
        42: invokevirtual #147                // Method java/io/DataOutputStream.writeInt:(I)V
        45: aload         4
        47: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        50: aload_2
        51: invokevirtual #299                // Method java/io/DataOutputStream.write:([B)V
        54: aload         4
        56: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        59: iload_3
        60: invokevirtual #301                // Method java/io/DataOutputStream.write:(I)V
        63: aload_0
        64: getfield      #40                 // Field a:LcL;
        67: aload         4
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        74
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        61: getstatic     #54                 // Field $np_BDPyBd:[I
        64: iconst_5
        65: iaload
        66: invokestatic  #212                // Method a:(B)LbR;
        69: dup
        70: astore        4
        72: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        75: aload_1
>       76: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        79: aload         4
        81: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        84: aload_2
>       85: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        88: aload         4
        90: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        93: aload_3
>       94: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        97: aload         4
        99: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       102: ldc_w         #343                // String
>      105: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
       108: aload         4
       110: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       113: ldc_w         #343                // String
>      116: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
       119: aload         4
       121: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       124: getstatic     #189                // Field $s_KvWtIL:Ljava/lang/String;
       127: invokestatic  #347                // Method RMS.b:(Ljava/lang/String;)Ljava/lang/String;
>      130: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
       133: aload         4
       135: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
       138: getstatic     #350                // Field GameMidlet.F:B
       141: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
       144: aload_0
       145: getfield      #40                 // Field a:LcL;
       148: aload         4
       150: invokevirtual #62                 // Method cL.t:(LbR;)V
       153: aload         4
       155: invokevirtual #67                 // Method bR.hx:()V
       158: return
       159: pop
       160: return
      Exception table:
         from    to  target type
            61   158   159   Class java/io/IOException
        24: dup
        25: astore_3
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: iload_2
        30: invokevirtual #147                // Method java/io/DataOutputStream.writeInt:(I)V
        33: aload_3
        34: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        37: aload_1
>       38: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        41: aload_0
        42: getfield      #40                 // Field a:LcL;
        45: aload_3
        46: invokevirtual #62                 // Method cL.t:(LbR;)V
        49: aload_3
        50: invokevirtual #67                 // Method bR.hx:()V
        53: return
        54: pop
        55: aload_3
        56: invokevirtual #67                 // Method bR.hx:()V
        59: return
        60: astore        4
        62: aload_3
        63: invokevirtual #67                 // Method bR.hx:()V
        66: aload         4
        68: athrow
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        76
        20: iaload
        21: invokestatic  #75                 // Method b:(B)LbR;
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_0
        34: getfield      #40                 // Field a:LcL;
        37: aload_2
        38: invokevirtual #62                 // Method cL.t:(LbR;)V
        41: aload_2
        42: invokevirtual #67                 // Method bR.hx:()V
        45: return
        46: pop
        47: aload_2
        48: invokevirtual #67                 // Method bR.hx:()V
        51: return
        52: astore_3
        53: aload_2
        54: invokevirtual #67                 // Method bR.hx:()V
        57: aload_3
        58: athrow
        29: dup
        30: astore_3
        31: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        34: iload_1
        35: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        38: aload_3
        39: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        42: aload_2
>       43: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        46: aload_0
        47: getfield      #40                 // Field a:LcL;
        50: aload_3
        51: invokevirtual #62                 // Method cL.t:(LbR;)V
        54: aload_3
        55: invokevirtual #67                 // Method bR.hx:()V
        58: return
        59: pop
        60: aload_3
        61: invokevirtual #67                 // Method bR.hx:()V
        64: return
        65: astore        4
        67: aload_3
        68: invokevirtual #67                 // Method bR.hx:()V
        71: aload         4
        73: athrow
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        61
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        62
        20: iaload
        21: invokestatic  #75                 // Method b:(B)LbR;
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_0
        34: getfield      #40                 // Field a:LcL;
        37: aload_2
        38: invokevirtual #62                 // Method cL.t:(LbR;)V
        41: aload_2
        42: invokevirtual #67                 // Method bR.hx:()V
        45: return
        46: pop
        47: aload_2
        48: invokevirtual #67                 // Method bR.hx:()V
        51: return
        52: astore_3
        53: aload_2
        54: invokevirtual #67                 // Method bR.hx:()V
        57: aload_3
        58: athrow
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        90
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        87
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        92
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        17: getstatic     #54                 // Field $np_BDPyBd:[I
        20: bipush        6
        22: iaload
        23: invokespecial #92                 // Method bR."<init>":(B)V
        26: dup
        27: astore        4
        29: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        32: aload_1
>       33: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        36: aload         4
        38: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        41: aload_2
>       42: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        45: aload         4
        47: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        50: aload_3
>       51: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        54: aload_0
        55: getfield      #40                 // Field a:LcL;
        58: aload         4
        60: invokevirtual #62                 // Method cL.t:(LbR;)V
        63: aload         4
        65: invokevirtual #67                 // Method bR.hx:()V
        68: return
        69: pop
        70: return
      Exception table:
         from    to  target type
            13    68    69   Class java/lang/Exception
  
    public final void ig();
      Code:
         0: getstatic     #52                 // Field $op_ZnP3P8:I
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        21
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_2
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_0
        38: getfield      #40                 // Field a:LcL;
        41: aload_2
        42: invokevirtual #62                 // Method cL.t:(LbR;)V
        45: aload_2
        46: invokevirtual #67                 // Method bR.hx:()V
        49: return
        50: pop
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: astore_3
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: aload_3
        62: athrow
        17: getstatic     #54                 // Field $np_BDPyBd:[I
        20: sipush        133
        23: iaload
        24: invokespecial #92                 // Method bR."<init>":(B)V
        27: dup
        28: astore        8
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload         8
        39: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        42: aload_2
>       43: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        46: aload         8
        48: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        51: aload_3
>       52: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        55: aload         8
        57: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        60: aload         4
>       62: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        65: aload         8
        67: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        70: aload         5
>       72: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        75: aload         8
        77: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        80: aload         6
>       82: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        85: aload         8
        87: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        90: aload         7
>       92: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        95: aload_0
        96: getfield      #40                 // Field a:LcL;
        99: aload         8
       101: invokevirtual #62                 // Method cL.t:(LbR;)V
       104: aload         8
       106: invokevirtual #67                 // Method bR.hx:()V
       109: return
       110: pop
       111: return
      Exception table:
         from    to  target type
            13   109   110   Class java/lang/Exception
  
    public final void ih();
      Code:
         0: getstatic     #52                 // Field $op_ZnP3P8:I
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        95
        20: iaload
        21: invokestatic  #58                 // Method c:(B)LbR;
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_0
        34: getfield      #40                 // Field a:LcL;
        37: aload_2
        38: invokevirtual #62                 // Method cL.t:(LbR;)V
        41: aload_2
        42: invokevirtual #67                 // Method bR.hx:()V
        45: return
        46: pop
        47: aload_2
        48: invokevirtual #67                 // Method bR.hx:()V
        51: return
        52: astore_3
        53: aload_2
        54: invokevirtual #67                 // Method bR.hx:()V
        57: aload_3
        58: athrow
        76: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        79: getstatic     #54                 // Field $np_BDPyBd:[I
        82: bipush        11
        84: iaload
        85: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        88: aload_2
        89: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        92: aload_1
>       93: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        96: goto          100
        99: pop
       100: aload_0
       101: getfield      #40                 // Field a:LcL;
       104: aload_2
       105: invokevirtual #62                 // Method cL.t:(LbR;)V
       108: return
      Exception table:
         from    to  target type
            75    96    99   Class java/lang/Exception
  
    public final void j(java.lang.String, java.lang.String);
      Code:
         0: getstatic     #52                 // Field $op_ZnP3P8:I
         3: getstatic     #52                 // Field $op_ZnP3P8:I
         6: if_icmpeq     13
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        93
        20: iaload
        21: invokestatic  #75                 // Method b:(B)LbR;
        24: dup
        25: astore_3
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_3
        34: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        37: aload_2
>       38: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        41: aload_0
        42: getfield      #40                 // Field a:LcL;
        45: aload_3
        46: invokevirtual #62                 // Method cL.t:(LbR;)V
        49: aload_3
        50: invokevirtual #67                 // Method bR.hx:()V
        53: return
        54: pop
        55: aload_3
        56: invokevirtual #67                 // Method bR.hx:()V
        59: return
        60: astore        4
        62: aload_3
        63: invokevirtual #67                 // Method bR.hx:()V
        66: aload         4
        68: athrow
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        108
        20: iaload
        21: invokestatic  #75                 // Method b:(B)LbR;
        24: dup
        25: astore_3
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_3
        34: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        37: iload_2
        38: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        41: aload_0
        42: getfield      #40                 // Field a:LcL;
        45: aload_3
        46: invokevirtual #62                 // Method cL.t:(LbR;)V
        49: aload_3
        50: invokevirtual #67                 // Method bR.hx:()V
        53: return
        54: pop
        55: aload_3
        56: invokevirtual #67                 // Method bR.hx:()V
        59: return
        60: astore        4
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        91
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_3
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_1
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_3
        38: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        41: aload_2
>       42: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        45: aload_0
        46: getfield      #40                 // Field a:LcL;
        49: aload_3
        50: invokevirtual #62                 // Method cL.t:(LbR;)V
        53: aload_3
        54: invokevirtual #67                 // Method bR.hx:()V
        57: return
        58: pop
        59: aload_3
        60: invokevirtual #67                 // Method bR.hx:()V
        63: return
        64: astore        4
        66: aload_3
        67: invokevirtual #67                 // Method bR.hx:()V
        70: aload         4
        72: athrow
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: getstatic     #159                // Field ba.gb:I
        32: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        35: aload_2
        36: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        39: aload_1
>       40: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        43: aload_0
        44: getfield      #40                 // Field a:LcL;
        47: aload_2
        48: invokevirtual #62                 // Method cL.t:(LbR;)V
        51: aload_2
        52: invokevirtual #67                 // Method bR.hx:()V
        55: return
        56: pop
        57: aload_2
        58: invokevirtual #67                 // Method bR.hx:()V
        61: return
        62: astore_3
        63: aload_2
        64: invokevirtual #67                 // Method bR.hx:()V
        67: aload_3
        68: athrow
        19: getstatic     #54                 // Field $np_BDPyBd:[I
        22: bipush        6
        24: iaload
        25: invokespecial #92                 // Method bR."<init>":(B)V
        28: dup
        29: astore_3
        30: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        33: aload_2
>       34: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        37: aload_3
        38: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        41: iload_1
        42: invokevirtual #83                 // Method java/io/DataOutputStream.writeShort:(I)V
        45: aload_0
        46: getfield      #40                 // Field a:LcL;
        49: aload_3
        50: invokevirtual #62                 // Method cL.t:(LbR;)V
        53: aload_3
        54: invokevirtual #67                 // Method bR.hx:()V
        57: return
        58: pop
        59: aload_3
        60: invokevirtual #67                 // Method bR.hx:()V
        63: return
        64: astore        4
        29: dup
        30: astore        4
        32: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        35: iload_1
        36: invokevirtual #83                 // Method java/io/DataOutputStream.writeShort:(I)V
        39: aload         4
        41: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        44: aload_2
>       45: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        48: aload         4
        50: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        53: iload_3
        54: invokevirtual #88                 // Method java/io/DataOutputStream.writeByte:(I)V
        57: aload_0
        58: getfield      #40                 // Field a:LcL;
        61: aload         4
        63: invokevirtual #62                 // Method cL.t:(LbR;)V
        66: aload         4
        68: invokevirtual #67                 // Method bR.hx:()V
        71: return
        72: pop
        73: aload         4
        75: invokevirtual #67                 // Method bR.hx:()V
        78: return
        79: astore        5
        15: getstatic     #54                 // Field $np_BDPyBd:[I
        18: bipush        107
        20: iaload
        21: invokestatic  #75                 // Method b:(B)LbR;
        24: dup
        25: astore_2
        26: invokevirtual #78                 // Method bR.a:()Ljava/io/DataOutputStream;
        29: aload_1
>       30: invokevirtual #150                // Method java/io/DataOutputStream.writeUTF:(Ljava/lang/String;)V
        33: aload_0
        34: getfield      #40                 // Field a:LcL;
        37: aload_2
        38: invokevirtual #62                 // Method cL.t:(LbR;)V
        41: aload_2
        42: invokevirtual #67                 // Method bR.hx:()V
        45: return
        46: pop
        47: aload_2
        48: invokevirtual #67                 // Method bR.hx:()V
        51: return
        52: astore_3
        53: aload_2
        54: invokevirtual #67                 // Method bR.hx:()V
        57: aload_3
        58: athrow
```

## Dispatcher command -30 detail

```text

                   124: 24469

                   125: 24482

                   126: 24564
               default: 741
          }
     700: aload_0
     701: aload_1
     702: invokespecial #1476               // Method h:(LbR;)V
     705: aload_1
     706: ifnull        713
     709: aload_1
     710: invokevirtual #1093               // Method bR.hx:()V
     713: return
     714: aload_1
     715: invokestatic  #1478               // Method g:(LbR;)V
     718: aload_1
     719: ifnull        726
     722: aload_1
     723: invokevirtual #1093               // Method bR.hx:()V
     726: return
     727: aload_0
     728: aload_1
     729: invokespecial #1480               // Method e:(LbR;)V
     732: aload_1
     733: ifnull        740
     736: aload_1
     737: invokevirtual #1093               // Method bR.hx:()V
     740: return
     741: aload_1
     742: ifnull        749
     745: aload_1
     746: invokevirtual #1093               // Method bR.hx:()V
     749: return
     750: aload_1
     751: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
     754: invokevirtual #225                // Method java/io/DataInputStream.readUTF:()Ljava/lang/String;
     757: astore        24
     759: new           #673                // class java/lang/StringBuilder
     762: dup
     763: getstatic     #263                // Field $s_FgCXw9:Ljava/lang/String;
     766: invokespecial #676                // Method java/lang/StringBuilder."<init>":(Ljava/lang/String;)V
     769: aload         24
     771: invokevirtual #683                // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
     774: ldc_w         #1482               // String \"
     777: invokevirtual #683                // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
     780: invokevirtual #895                // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
     783: invokestatic  #908                // Method dt.g:(Ljava/lang/String;)V
     786: aload         24
     788: ifnull        829
     791: aload         24
     793: getstatic     #265                // Field $s_9LBgsk:Ljava/lang/String;
     796: invokevirtual #1357               // Method java/lang/String.indexOf:(Ljava/lang/String;)I
     799: iflt          829
     802: new           #673                // class java/lang/StringBuilder
     805: dup
     806: getstatic     #267                // Field $s_NqkeLh:Ljava/lang/String;
     809: invokespecial #676                // Method java/lang/StringBuilder."<init>":(Ljava/lang/String;)V
     812: aload         24
     814: invokevirtual #683                // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
     817: ldc_w         #1482               // String \"
     820: invokevirtual #683                // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
     823: invokevirtual #895                // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
     826: invokestatic  #1484               // Method f.h:(Ljava/lang/String;)V
     829: aload         24
     831: ifnull        933
     834: aload         24
     836: getstatic     #167                // Field s:[Ljava/lang/String;
     839: getstatic     #159                // Field $np_yHPk8p:[I
     842: iconst_1
     843: iaload
     844: aaload
     845: invokevirtual #1261               // Method java/lang/String.startsWith:(Ljava/lang/String;)Z
     848: ifeq          933
     851: aload         24
     853: getstatic     #167                // Field s:[Ljava/lang/String;
     856: getstatic     #159                // Field $np_yHPk8p:[I
     859: iconst_0
     860: iaload
     861: aaload
     862: invokevirtual #1357               // Method java/lang/String.indexOf:(Ljava/lang/String;)I
     865: dup
     866: istore        61
     868: getstatic     #167                // Field s:[Ljava/lang/String;
     871: getstatic     #159                // Field $np_yHPk8p:[I
     874: iconst_1
     875: iaload
     876: aaload
     877: invokevirtual #1360               // Method java/lang/String.length:()I
     880: if_icmple     929
     883: aload         24
     885: getstatic     #167                // Field s:[Ljava/lang/String;
     888: getstatic     #159                // Field $np_yHPk8p:[I
     891: iconst_1
     892: iaload
     893: aaload
     894: invokevirtual #1360               // Method java/lang/String.length:()I
```

## Command -30 handler al.h(bR)

```text
  private void h(bR);
    Code:
       0: getstatic     #197                // Field $op_tgaLUZ:I
       3: getstatic     #197                // Field $op_tgaLUZ:I
       6: if_icmpeq     13
       9: getstatic     #197                // Field $op_tgaLUZ:I
      12: pop
      13: aload_1
      14: invokevirtual #499                // Method bR.a:()Ljava/io/DataInputStream;
      17: invokevirtual #203                // Method java/io/DataInputStream.readByte:()B
      20: tableswitch   { // -128 to 115

                  -128: 1012

                  -127: 1082

                  -126: 3489

                  -125: 3663

                  -124: 4103

                  -123: 4250

                  -122: 4370

                  -121: 4392

                  -120: 4414

                  -119: 4446

                  -118: 4504

                  -117: 4513

                  -116: 4626

                  -115: 4504

                  -114: 4504

                  -113: 4739

                  -112: 4852

                  -111: 4934

                  -110: 4980

                  -109: 5099

                  -108: 4504

                  -107: 5263

                  -106: 5278

                  -105: 5293

                  -104: 5334

                  -103: 4504

                  -102: 5378

                  -101: 5628

                  -100: 6262

                   -99: 6476

                   -98: 7119

                   -97: 7615

                   -96: 7818

                   -95: 8354

                   -94: 8390

                   -93: 4504

                   -92: 8476

                   -91: 8607

                   -90: 8733

                   -89: 8870

                   -88: 4504

                   -87: 8916

                   -86: 9026

                   -85: 9068

                   -84: 9161

                   -83: 9219

                   -82: 9343

                   -81: 9435

                   -80: 9464

                   -79: 4504

                   -78: 9488

                   -77: 9524

                   -76: 9590

                   -75: 9618

                   -74: 9642

                   -73: 9661

                   -72: 9706

                   -71: 9728

                   -70: 4504

                   -69: 9867

                   -68: 10054

                   -67: 4504

                   -66: 4504

                   -65: 10261

                   -64: 10399

                   -63: 10493

                   -62: 10717

                   -61: 10814

                   -60: 4504

                   -59: 11024

                   -58: 11089

                   -57: 11149

                   -56: 11175

                   -55: 11237

                   -54: 11299

                   -53: 4504

                   -52: 4504

                   -51: 4504

                   -50: 4504

                   -49: 4504

                   -48: 4504

                   -47: 4504

                   -46: 4504

                   -45: 4504

                   -44: 4504

                   -43: 4504

                   -42: 4504

                   -41: 4504

                   -40: 4504

                   -39: 4504

                   -38: 4504

                   -37: 4504

                   -36: 4504

                   -35: 4504

                   -34: 4504

                   -33: 4504

                   -32: 4504

                   -31: 4504

                   -30: 4504

                   -29: 4504

                   -28: 4504

                   -27: 4504

                   -26: 4504

                   -25: 4504

                   -24: 4504

                   -23: 4504

                   -22: 4504

                   -21: 4504

                   -20: 4504

                   -19: 4504

                   -18: 4504

                   -17: 4504

                   -16: 4504

                   -15: 4504

                   -14: 4504

                   -13: 4504

                   -12: 4504

                   -11: 4504

                   -10: 4504

                    -9: 4504

                    -8: 4504

                    -7: 4504

                    -6: 4504

                    -5: 4504

                    -4: 4504

                    -3: 4504

                    -2: 4504

                    -1: 4504

                     0: 4504

                     1: 4504

                     2: 4504

                     3: 4504

                     4: 4504

                     5: 4504

                     6: 4504

                     7: 4504

                     8: 4504

                     9: 4504

                    10: 4504

                    11: 4504

                    12: 4504

                    13: 4504

                    14: 4504

                    15: 4504

                    16: 4504

                    17: 4504

                    18: 4504

                    19: 4504

                    20: 4504

                    21: 4504

                    22: 4504

                    23: 4504

                    24: 4504

                    25: 4504

                    26: 4504

                    27: 4504

                    28: 4504

                    29: 4504

                    30: 4504

                    31: 4504

                    32: 4504

                    33: 4504

                    34: 4504

                    35: 4504

                    36: 4504

                    37: 4504

                    38: 4504

                    39: 4504

                    40: 4504

                    41: 4504
```
