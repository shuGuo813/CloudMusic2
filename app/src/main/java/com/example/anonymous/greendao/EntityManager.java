package com.example.anonymous.greendao;

public class EntityManager {
    private static EntityManager entityManager;
    public MusicCloudBeanDao cloudbeanDao;
    public MusicListBeanDao listBeanDao;
    public PlayMusicBeanDao playMusicBeanDao;
    /**
     * 创建User表实例
     *
     * @return
     */
    public MusicCloudBeanDao getUserDao(){
        cloudbeanDao = DaoManager.getInstance().getSession().getMusicCloudBeanDao();
        return cloudbeanDao;
    }
    public MusicListBeanDao getUserListDao(){
        listBeanDao = DaoManager.getInstance().getSession().getMusicListBeanDao();
        return listBeanDao;
    }
    public PlayMusicBeanDao getPlayMusicDao(){
        playMusicBeanDao = DaoManager.getInstance().getSession().getPlayMusicBeanDao();
        return playMusicBeanDao;
    }

    /**
     * 创建单例
     *
     * @return
     */
    public static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }
}
