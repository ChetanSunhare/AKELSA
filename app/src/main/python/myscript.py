import wolframalpha
import wikipedia
client = wolframalpha.Client('8G7TUH-UH5P3KUYJE')
def searchOnPython(cmd):
    query = cmd.lower();
    if 'wikipedia' in query:
                query = query.replace("wikipedia", "")
                results = wikipedia.summary(query, sentences=2)
                return results
    elif 'who is' in query:
                query = query.replace("wikipedia", "")
                results = wikipedia.summary(query, sentences=2)
                return results
    else:
        try:
            query = query
            res = client.query(query)
            results = next(res.results).text
            return results
        except:
            return "did not understand.. please try again."
