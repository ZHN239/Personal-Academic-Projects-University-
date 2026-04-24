using UnityEngine;

public class background : MonoBehaviour
{
    private Transform tr;
    public float speed = 1.8f;
    // Start is called once before the first execution of Update after the MonoBehaviour is created
    void Start()
    {
        tr=GetComponent<Transform>();
    }

    // Update is called once per frame
    void Update()
    {
        foreach (Transform trans in tr)
        {
            trans.position-= new Vector3(speed * Time.deltaTime, 0, 0);
            if (trans.position.x < -7.2f)
            {
                trans.position += new Vector3(7.2f * 2, 0, 0);
            }
        }
    }
}
