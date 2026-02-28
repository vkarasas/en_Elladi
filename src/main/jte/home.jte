<%--@param gg.jte.Content content--%>
@param java.util.List<com.enElladi.models.Channel> channels
@param int count = 1
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EnElladi Website</title>

    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
            rel="stylesheet"
            integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB"
            crossorigin="anonymous"
    >
</head>

<body class="bg-light">

<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand d-flex align-items-center" href="/home">ἔν Ἑλλάδι</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#mainNavbar" aria-controls="mainNavbar"
                aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNavbar">
<%--            <ul class="navbar-nav ms-auto mb-2 mb-lg-0">--%>
<%--                <li class="nav-item">--%>
<%--                    <a class="nav-link" href="/home">Home</a>--%>
<%--                </li>--%>
<%--            </ul>--%>
        </div>
    </div>

</nav>

<%--<!-- Main content -->--%>
<main class="container py-4">
<%--    ${content}--%>

    <table class="table table-bordered table-hover align-middle mb-0">
        <thead class="table-light sticky-top">
        <tr>
            <th class="text-center" style="width: 60px;">#</th>
            <th class="text-center">Category</th>
            <th class="text-center">Website</th>
        </tr>
        </thead>
        @for (int i = 0; i < channels.size(); i++)
            @for(int j = 0; j < channels.get(i).getMessages().size(); j++)
                <tr>
                    <td class="text-center">${count++}</td>
                    <td class="text-center">${channels.get(i).getName()}</td>
                    <td class="text-center"><a href="${channels.get(i).getMessages().get(j).getContent()}" target="_blank">${channels.get(i).getMessages().get(j).getContent()}</a></td>
                </tr>
            @endfor
        @endfor
    </table>
</main>
</body>
</html>